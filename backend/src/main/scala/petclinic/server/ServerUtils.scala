package petclinic.server

import petclinic.models.PetId
import zio.IO

object ServerUtils {
  def parsePetId(id: String): IO[AppError.InvalidIdError, PetId] =
    PetId.fromString(id).orElseFail(AppError.InvalidIdError("Invalid pet id"))
}
