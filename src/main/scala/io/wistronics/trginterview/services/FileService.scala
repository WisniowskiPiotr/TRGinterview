package io.wistronics.trginterview.services

import java.net.URI

import zio.{ Has, Task, UIO, ULayer, ZIO, ZLayer }

import scala.io.Source

trait FileService {
  def read(uri: URI): ZIO[Any, Throwable, String]
}

object FileService {
  case class FromResource() extends FileService {
    override def read(uri: URI): ZIO[Any, Throwable, String] =
      Task {
        val relativePath = uri.getPath.split("test-classes").last
        Source.fromURL(getClass.getResource(relativePath))
      }.bracket(f => UIO(f.close()))(f => UIO(f.mkString))
  }
  object FromResource {
    def layer: ULayer[Has[FileService]] = ZLayer.succeed(FromResource())
  }
}
