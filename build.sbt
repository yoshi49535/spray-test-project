lazy val typesafeConfig = "com.typesafe" % "config"               % "1.2.1"
lazy val mysql          = "mysql"        % "mysql-connector-java" % "5.1.35"
lazy val slick          = "com.typesafe.slick" %% "slick"         % "3.0.0"
lazy val c3p0           = "com.mchange"  % "c3p0"             % "0.9.5"

lazy val commonSettings = Seq(
  organization := "jp.co.o3",
  scalaVersion := "2.10.5",
  version := "0.1.0",
  scalacOptions := Seq("-unchecked", "-deprecation", "-feature", "-encoding", "utf8"),
  libraryDependencies ++= {
    val akkaV = "2.3.9"
    val sprayV = "1.3.3"
    Seq(
      "io.spray"            %%  "spray-can"     % sprayV,
      "io.spray"            %%  "spray-routing" % sprayV,
      "io.spray"            %%  "spray-testkit" % sprayV  % "test",
      "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
      "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
      "org.json4s"          %%  "json4s-native" % "3.2.4"
    )
  }
)

lazy val consoleSetting = commonSettings ++ {
    libraryDependencies += typesafeConfig
    libraryDependencies += mysql
    libraryDependencies += slick
  }

lazy val scalaCanSetting = commonSettings ++ {
    libraryDependencies ++= {
      val akkaV = "2.3.9"
      val sprayV = "1.3.3"
      Seq(
        "io.spray"            %%  "spray-can"     % sprayV,
        "io.spray"            %%  "spray-routing" % sprayV,
        "io.spray"            %%  "spray-testkit" % sprayV  % "test",
        "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
        "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
        "org.specs2"          %%  "specs2-core"   % "2.3.7" % "test",
        "io.spray"            %%  "spray-json"    % "1.3.1",
        "com.github.mauricio" %%  "mysql-async"   % "0.2.15",
        "org.slf4j"           % "slf4j-log4j12" % "1.7.12",
        typesafeConfig,
        mysql,
        c3p0,
        slick
      )
    }
  }


lazy val root = (project in file(".")).
  aggregate(core, dictionaryService).
  settings(
    name := "root",
    aggregate in update := false
  )


// Include all projects to test and debug
lazy val deployRouteAll = Project("deploy-route-all", file("deploy/route-all")).
  settings(scalaCanSetting: _*).
  settings(
    name := "deploy-route-all"
  ).
  dependsOn(core, dictionaryService)


lazy val migrateTool = Project("migration-tool", file("tools/migrate")).
  settings(consoleSetting: _*).
  settings(
    sbtPlugin := true,
    name := "migrate-tool"
  ).
  dependsOn(core, dictionaryService)


// libs/core pacakge 
lazy val core = (project in file("libs/core")).
  settings(commonSettings: _*).
  settings(
    sbtPlugin := true,
    name := "core",
    libraryDependencies += typesafeConfig
  )


lazy val dictionaryService = Project("dictionary-service", file("services/dictionary")).
  settings(scalaCanSetting: _*).
  settings(
    sbtPlugin := true,
    name := "dictionary-server",
    libraryDependencies += mysql 
  ).
  dependsOn(core)

// revolver for "root" project
Revolver.settings


resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

