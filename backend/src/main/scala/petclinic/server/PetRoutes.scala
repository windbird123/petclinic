package petclinic.server

import petclinic.server.ServerUtils._
import petclinic.services.PetService
import zhttp.http._
import zio._
import zio.json._

final case class PetRoutes(service: PetService) {
  val routes: Http[Any, Throwable, Request, Response] = Http.collectZIO[Request] {

    // Gets a single Pet found by their parsed ID and returns it as JSON.
    case Method.GET -> !! / "pets" / id =>
      for {
        id  <- parsePetId(id)
        pet <- service.get(id)
      } yield Response.json(pet.toJson)
  }
}

object PetRoutes {
  val layer: URLayer[PetService, PetRoutes] = ZLayer.fromFunction(PetRoutes.apply _)
}
