package petclinic

import org.postgresql.ds.PGSimpleDataSource
import petclinic.server.{ClinicServer, PetRoutes, RootRoutes}
import petclinic.services.PetServiceLive
import zio.http.Server
import zio.{Task, ULayer, ZIO, ZIOAppDefault, ZLayer}

import javax.sql.DataSource

object Main extends ZIOAppDefault {
  val dataSourceLayer: ULayer[DataSource] = ZLayer.succeed(new PGSimpleDataSource)
  override def run: Task[Unit] = ZIO
    .serviceWithZIO[ClinicServer](_.start)
    .provide(Server.default, ClinicServer.layer, PetRoutes.layer, RootRoutes.layer, PetServiceLive.layer, dataSourceLayer)
}
