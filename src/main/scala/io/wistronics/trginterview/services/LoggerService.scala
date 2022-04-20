package io.wistronics.trginterview.services

import zio.logging.slf4j.Slf4jLogger
import zio.{ Has, ZLayer }
import zio.logging.{ Logger, Logging }

object LoggerService {
  object Log4j {
    def layer(clazz: Class[_]): ZLayer[Any, Nothing, Has[Logger[String]]] = layer(clazz.getName)
    def layer(className: String): ZLayer[Any, Nothing, Has[Logger[String]]] =
      Slf4jLogger.makeWithAllAnnotationsAsMdc() >>> Logging.withRootLoggerName(className)
  }
}
