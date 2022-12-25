package petclinic.server

import petclinic.server.ServerUtils._
import petclinic.services.PetService
import zio.http._
import zio.http.model.Method
import zio.{ULayer, URLayer, ZIO, ZLayer}
import zio.json._

import java.io.File

final case class RootRoutes() {
//  val routes: Http[Any, Throwable, Request, Response] = Http.collectZIO[Request] {
//
//    // Gets a single Pet found by their parsed ID and returns it as JSON.
//    case Method.GET -> "" /: path =>
//      ZIO.succeed(Response.text(s"Hello 2 [$path]"))
//  }

  val routes: Http[Any, Throwable, Request, Response] = Http.collectHttp[Request] {
    case Method.GET -> "" /: path =>
      println(path)
      Http.fromFile(new File(s"../dist/$path"))
  }
}

object RootRoutes {
  val layer: ULayer[RootRoutes] = ZLayer.fromFunction(RootRoutes.apply _)
}
