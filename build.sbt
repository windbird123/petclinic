ThisBuild / scalaVersion     := "2.13.10"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.github.windbird123.petclinic"
ThisBuild / organizationName := "windbird123"

val flywayVersion               = "8.5.12"     // manages database migrations
val laminarVersion              = "0.14.2"     // functional reactive programming (FRP) library
val waypointVersion             = "0.5.0"      // router for Laminar for URL matching and managing URL transitions
val postgresVersion             = "42.3.6"     // Java database connectivity (JDBC) driver for PostgreSQL
val scalaJavaTimeVersion        = "2.4.0"      // an implementation of the java.time package for Scala
val slf4jVersion                = "1.7.36"     // logging framework
val zioHttpVersion              = "0.0.3"      // HTTP client library for ZIO
val zioJsonVersion              = "0.3.0-RC8"  // JSON serialization library for ZIO
val zioLoggingVersion           = "2.0.0-RC10" // logging library for ZIO
val zioTestContainersVersion    = "0.6.0"      // library fro testing database queries with ZIO
val zioVersion                  = "2.0.0-RC6"  // Scala library for asynchronous and concurrent programming
val zioMetricsConnectorsVersion = "2.0.0-RC6"  // metrics library for ZIO

Global / onChangedBuildSource := ReloadOnSourceChanges

val sharedSettings = Seq(
  libraryDependencies ++= Seq(
    "dev.zio" %%% "zio-json" % zioJsonVersion
  ),
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding",
    "utf8",
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-unchecked",
    "-Xfatal-warnings",
    "-Ymacro-annotations"
  )
)

val publicDev  = taskKey[String]("output directory for `npm run dev`")
val publicProd = taskKey[String]("output directory for `npm run build`")

lazy val root = (project in file("."))
  .aggregate(backend, frontend, shared)
  .settings(name := "petclinic")

lazy val backend = (project in file("backend"))
  .settings(
    name := "backend",
    libraryDependencies ++= Seq(
      "dev.zio"               %% "zio"                               % zioVersion,
      "dev.zio"               %% "zio-macros"                        % zioVersion,
      "dev.zio"               %% "zio-metrics-connectors"            % zioMetricsConnectorsVersion,
      "dev.zio"               %% "zio-test"                          % zioVersion % Test,
      "dev.zio"               %% "zio-test-sbt"                      % zioVersion % Test,
      "dev.zio"               %% "zio-http"                          % zioHttpVersion,
      "org.postgresql"         % "postgresql"                        % postgresVersion,
      "org.flywaydb"           % "flyway-core"                       % flywayVersion,
      "io.github.scottweaver" %% "zio-2-0-testcontainers-postgresql" % zioTestContainersVersion,
      "io.github.scottweaver" %% "zio-2-0-db-migration-aspect"       % zioTestContainersVersion,
      "dev.zio"               %% "zio-logging-slf4j"                 % zioLoggingVersion,
      "org.slf4j"              % "slf4j-api"                         % slf4jVersion,
      "org.slf4j"              % "slf4j-simple"                      % slf4jVersion
    ),
    Test / fork := true,
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
  .enablePlugins(JavaAppPackaging)
  .settings(sharedSettings)
  .enablePlugins(FlywayPlugin)
  .settings(
    flywayUrl      := "jdbc:postgresql://localhost:5432/postgres",
    flywayUser     := "postgres",
    flywayPassword := ""
  )
  .dependsOn(shared)

lazy val frontend = (project in file("frontend"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name                            := "frontend",
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },
    scalaJSLinkerConfig ~= { _.withSourceMap(false) },
    libraryDependencies ++= Seq(
      "com.raquo"         %%% "laminar"         % laminarVersion,
      "com.raquo"         %%% "waypoint"        % waypointVersion,
      "io.github.cquiroz" %%% "scala-java-time" % scalaJavaTimeVersion
    ),
    publicDev  := linkerOutputDirectory((Compile / fastLinkJS).value).getAbsolutePath,
    publicProd := linkerOutputDirectory((Compile / fullLinkJS).value).getAbsolutePath
  )
  .settings(sharedSettings)
  .dependsOn(shared)

lazy val shared = (project in file("shared"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    scalaJSLinkerConfig ~= { _.withSourceMap(false) },
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) }
  )
  .settings(sharedSettings)

def linkerOutputDirectory(v: Attributed[org.scalajs.linker.interface.Report]): File =
  v.get(scalaJSLinkerOutputDirectory.key).getOrElse {
    throw new MessageOnlyException(
      "Linking report was not attributed with output directory. " +
        "Please report this as a Scala.js bug."
    )
  }
