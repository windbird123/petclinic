package petclinic.server

import zio.http.middleware.HttpMiddleware
import zio.http.{Http, HttpApp, Middleware, Request, Response, Server}
import zio.{Random, System, ZIO, ZLayer}

/** ClinicServer is a service that will start up the ZIO-Http server.
  *
  * It is comprised of the various routes, which in this case are also services
  * that we defined in the different route files
  */
final case class ClinicServer(
    petRoutes: PetRoutes,
    rootRoutes: RootRoutes
) {
  val allRoutes: HttpApp[Any, Throwable] = petRoutes.routes ++ rootRoutes.routes

  /** Logs the requests made to the server.
    *
    * It also adds a request ID to the logging context, so any further logging
    * that occurs in the handler can be associated with the same request.
    *
    * For more information on the logging, see:
    * https://zio.github.io/zio-logging/
    */
//  val loggingMiddleware: HttpMiddleware[Any, Nothing] =
//    new HttpMiddleware[Any, Nothing] {
//       def apply[R1 <: Any, E1 >: Nothing](
//          http: Http[R1, E1, Request, Response]
//      ): Http[R1, E1, Request, Response] =
//        Http.fromOptionFunction[Request] { request =>
//          Random.nextUUID.flatMap { requestId =>
//            ZIO.logAnnotate("REQUEST-ID", requestId.toString) {
//              for {
//                _      <- ZIO.logInfo(s"Request: $request")
//                result <- http(request)
//              } yield result
//            }
//          }
//        }
//    }

  /** Resets the database to the initial state every 15 minutes to clean up the
    * deployed Heroku data. Then, it obtains a port from the environment on
    * which to start the server. In the case of being run in production, the
    * port will be provided by Heroku, otherwise the port will be 8080. The
    * server is then started on the given port with the routes provided.
    */
  def start =
    for {
      port <- System.envOrElse("PORT", "8080").map(_.toInt)
//      _    <- Server.serve(port, allRoutes @@ Middleware.cors())
      _    <- Server.serve(allRoutes)
    } yield ()
}

object ClinicServer {
  val layer: ZLayer[PetRoutes with RootRoutes, Nothing, ClinicServer] = ZLayer.fromFunction(ClinicServer.apply _)
}
