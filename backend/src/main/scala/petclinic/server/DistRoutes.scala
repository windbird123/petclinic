package petclinic.server

import zio.http._
import zio.http.model.Method
import zio.{ULayer, ZLayer}

final case class DistRoutes() {
  val routes: Http[Any, Throwable, Request, Response] = Http.collectHttp[Request] { case Method.GET -> "" /: path =>
    Http.fromResource(s"dist/$path")
  }
}

object DistRoutes {
  val layer: ULayer[DistRoutes] = ZLayer.fromFunction(DistRoutes.apply _)
}
