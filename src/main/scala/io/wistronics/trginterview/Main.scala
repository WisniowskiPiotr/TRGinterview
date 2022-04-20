package io.wistronics.trginterview

import java.net.URI

import io.wistronics.trginterview.services.{ FileService, LoggerService, SparkService }
import org.apache.spark.sql.SparkSession
import zio.{ BootstrapRuntime, ExitCode, Has, Task, URIO, ZIO }
import zio.logging.{ log, Logger }

object Main extends BootstrapRuntime {
  val appName = "TRGInterview"

  final def main(args: Array[String]): Unit = unsafeRun(run(args.toList)) match {
    case ExitCode.success => Unit
    case exit =>
      throw new RuntimeException(s"Failed with '$exit'. Take a look on previously reported errors.") // scalafix:ok DisableSyntax.throw
  }

  def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    app(args).provideLayer(
      LoggerService.Log4j.layer(this.getClass) ++
      SparkService.Local.layer ++
      FileService.FromResource.layer
    )

  def app(
    queriesPaths: List[String]
  ): ZIO[Has[Logger[String]] with Has[SparkService] with Has[FileService], Nothing, ExitCode] = {
    for {
      _ <- log.info(s"Got arguments: $queriesPaths")
      queries = queriesPaths.map(s => new URI(s))
      sqls <- ZIO.foldLeft(queries)("") {
        case (s, path) => ZIO.serviceWith[FileService](_.read(path).map(s + "\n;\n" + _))
      }
      _ <- ZIO
        .serviceWith[SparkService](_.getOrCreate(appName, Map.empty))
        .bracket(spark => ZIO.serviceWith[SparkService](_.close(spark)), spark => executeSql(spark, sqls))
    } yield ExitCode.success
  }.either.flatMap {
    case Left(throwable) =>
      for {
        _ <- log.error(throwable.getMessage)
      } yield ExitCode.failure
    case Right(exitCode) => ZIO.succeed(exitCode)
  }

  def executeSql(spark: SparkSession, sql: String): ZIO[Has[Logger[String]], Throwable, Unit] =
    ZIO
      .foreach(sql.split(";").map(_.trim).filter(_.replaceAll("(--.*)|(\\s*)", "").nonEmpty)) { sqlPart =>
        for {
          _ <- log.info(s"Executing:\n$sqlPart")
          out <- Task(spark.sql(sqlPart).collect())
          _ <- log.info(out.mkString("Returned: Array[\n", ", \n", "\n]"))
        } yield ()
      }
      .map(_ => Unit)

}
