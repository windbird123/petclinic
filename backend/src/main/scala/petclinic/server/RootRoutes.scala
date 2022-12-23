package petclinic.server

import petclinic.server.ServerUtils._
import petclinic.services.PetService
import zhttp.http._
import zio.{ULayer, URLayer, ZIO, ZLayer}
import zio.json._

final case class RootRoutes() {
  val routes: Http[Any, Throwable, Request, Response] = Http.collectZIO[Request] {

    // Gets a single Pet found by their parsed ID and returns it as JSON.
    case Method.GET -> !! / "root" =>
      ZIO.succeed(Response.text("Hello"))

  }
}

object RootRoutes {
  val layer: ULayer[RootRoutes] = ZLayer.fromFunction(RootRoutes.apply _)
}
