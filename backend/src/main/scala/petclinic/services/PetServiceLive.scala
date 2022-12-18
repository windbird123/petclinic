package petclinic.services
import petclinic.models.Species.Feline
import petclinic.models.{OwnerId, Pet, PetId, Species}
import zio.metrics.Metric
import zio.{Task, URLayer, ZIO, ZLayer}

import java.time.LocalDate
import java.util.UUID
import javax.sql.DataSource

case class PetServiceLive(dataSource: DataSource) extends PetService {

  /** Creates a new Pet. */
  override def create(name: String, birthdate: LocalDate, species: Species, ownerId: OwnerId): Task[Pet] = for {
    pet <- Pet.make(name, birthdate, species, ownerId)
    // inserts pet into DB using dataSource
    _ <- Metric.counter("pet.created").increment
  } yield pet

  /** Retrieves a Pet from the database. */
  override def get(id: PetId): Task[Option[Pet]] =
    ZIO.some(Pet(id, "baba", LocalDate.now(), Feline, OwnerId(UUID.randomUUID())))
}

object PetServiceLive {
  val layer: URLayer[DataSource, PetService] = ZLayer.fromFunction(PetServiceLive.apply _)
}
