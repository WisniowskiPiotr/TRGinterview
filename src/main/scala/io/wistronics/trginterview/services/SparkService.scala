package io.wistronics.trginterview.services

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import zio.{ Has, Task, UIO, ULayer, ZIO, ZLayer }

import scala.util.Random

trait SparkService {
  def getOrCreate(qppName: String, engineConfigs: Map[String, String]): ZIO[Any, Throwable, SparkSession]
  def close(spark: SparkSession): ZIO[Any, Nothing, Unit]
}

object SparkService {
  def getConfig(appName: String, engineConfigs: Map[String, String]): SparkConf =
    new SparkConf()
      .set("spark.app.name", appName)
      .set("spark.sql.session.timeZone", "UTC")
      .set("spark.sql.sources.partitionOverwriteMode", "dynamic")
      .set("hive.exec.dynamic.partition", "true")
      .set("hive.exec.dynamic.partition.mode", "nonstrict")
      .set("spark.eventLog.enabled", "false")
      .setAll(engineConfigs)

  object Local {
    val layer: ULayer[Has[SparkService]] = ZLayer.succeed(Local())
  }

  // this implementation most likely would be present in test - not on main - but just to save time
  case class Local() extends SparkService {
    override def getOrCreate(appName: String, engineConfigs: Map[String, String]): ZIO[Any, Throwable, SparkSession] =
      Task {
        val dbName = s"hive_metastore_db_${Random.nextInt()}".replace('-', '_')
        val spark = SparkSession
          .builder()
          .config(
            getConfig(
              appName,
              engineConfigs + (
                "javax.jdo.option.ConnectionURL" -> s"jdbc:derby:;databaseName=$dbName;create=true", // this creates a single derby db for each session
                "spark.sql.warehouse.dir" -> s"${System.getProperty("user.dir")}/target/spark-warehouse/$dbName",
                "spark.sql.catalogImplementation" -> "in-memory"
              )
            )
          )
          .enableHiveSupport()
          .master("local[3]")
          .getOrCreate()
        spark.sessionState.conf.setConfString("SparkService.dbname", dbName)
        spark.sql(s"DROP DATABASE IF EXISTS $dbName CASCADE").collect()
        spark.sql(s"CREATE DATABASE $dbName").collect()
        spark.sql(s"USE $dbName").collect()
        spark
      }

    override def close(spark: SparkSession): ZIO[Any, Nothing, Unit] = UIO {
      val dbName = spark.sessionState.conf.getConfString("SparkService.dbname")
      spark.sql(s"DROP DATABASE IF EXISTS $dbName CASCADE").collect()
      spark.close()
    }
  }
}
