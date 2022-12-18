package petclinic.server

import zhttp.http.middleware.HttpMiddleware
import zhttp.http.{Http, HttpApp, Middleware, Request, Response}
import zhttp.service.Server
import zio.{Random, System, ZIO, ZLayer}

/** ClinicServer is a service that will start up the ZIO-Http server.
  *
  * It is comprised of the various routes, which in this case are also services
  * that we defined in the different route files
  */
final case class ClinicServer(
    petRoutes: PetRoutes
) {
  val allRoutes: HttpApp[Any, Throwable] = petRoutes.routes

  /** Logs the requests made to the server.
    *
    * It also adds a request ID to the logging context, so any further logging
    * that occurs in the handler can be associated with the same request.
    *
    * For more information on the logging, see:
    * https://zio.github.io/zio-logging/
    */
  val loggingMiddleware: HttpMiddleware[Any, Nothing] =
    new HttpMiddleware[Any, Nothing] {
      override def apply[R1 <: Any, E1 >: Nothing](
          http: Http[R1, E1, Request, Response]
      ): Http[R1, E1, Request, Response] =
        Http.fromOptionFunction[Request] { request =>
          Random.nextUUID.flatMap { requestId =>
            ZIO.logAnnotate("REQUEST-ID", requestId.toString) {
              for {
                _      <- ZIO.logInfo(s"Request: $request")
                result <- http(request)
              } yield result
            }
          }
        }
    }

  /** Resets the database to the initial state every 15 minutes to clean up the
    * deployed Heroku data. Then, it obtains a port from the environment on
    * which to start the server. In the case of being run in production, the
    * port will be provided by Heroku, otherwise the port will be 8080. The
    * server is then started on the given port with the routes provided.
    */
  def start: ZIO[Any, Throwable, Unit] =
    for {
      port <- System.envOrElse("PORT", "8080").map(_.toInt)
      _    <- Server.start(port, allRoutes @@ Middleware.cors() @@ loggingMiddleware)
    } yield ()
}

object ClinicServer {
  val layer: ZLayer[PetRoutes, Nothing, ClinicServer] = ZLayer.fromFunction(ClinicServer.apply _)
}
