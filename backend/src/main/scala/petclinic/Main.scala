package petclinic

import org.postgresql.ds.PGSimpleDataSource
import petclinic.server.{ClinicServer, PetRoutes, RootRoutes}
import petclinic.services.PetServiceLive
import zio.http.{Server, ServerConfig}
import zio.{Task, ULayer, ZIO, ZIOAppDefault, ZLayer}

import java.net.InetSocketAddress
import javax.sql.DataSource

object Main extends ZIOAppDefault {
  val dataSourceLayer: ULayer[DataSource] = ZLayer.succeed(new PGSimpleDataSource)

  val myServer = ZLayer.succeed(ServerConfig(address = new InetSocketAddress(8080))) >>> Server.live
  override def run: Task[Unit] = ZIO
    .serviceWithZIO[ClinicServer](_.start)
    .provide(myServer, ClinicServer.layer, PetRoutes.layer, RootRoutes.layer, PetServiceLive.layer, dataSourceLayer)
//    .provide(Server.default, ClinicServer.layer, PetRoutes.layer, RootRoutes.layer, PetServiceLive.layer, dataSourceLayer)
}
