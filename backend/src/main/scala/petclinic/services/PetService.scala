package petclinic.services

import petclinic.models._
import zio._
import zio.macros._

import java.time.LocalDate

/** PetService manages the CRUD operations for the Pet type.
  *
  * Services like this are responsible for persisting and modifying saved data.
  * Note that the `@accessible` macro annotation is used to add "accessors" to
  * the companion object for use in the corresponding spec.
  */
@accessible
trait PetService {

  /** Creates a new Pet. */
  def create(name: String, birthdate: LocalDate, species: Species, ownerId: OwnerId): Task[Pet]

  /** Retrieves a Pet from the database. */
  def get(id: PetId): Task[Option[Pet]]
}
