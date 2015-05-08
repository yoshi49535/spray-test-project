lazy val commonSettings = Seq(
  organization := "jp.co.o3",
  scalaVersion := "2.10.5",
  version := "0.1.0",
  scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")
)


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
        "org.json4s"          %%  "json4s-native" % "3.2.4",
        "io.spray"            %%  "spray-json"    % "1.3.1",
        "com.github.mauricio" %%  "mysql-async"   % "0.2.15"
      )
    }
  }


// Include all projects to test and debug
lazy val root = (project in file(".")).
  settings(scalaCanSetting: _*).
  settings(
    name := "root"
  ).
  dependsOn(core, dictionaryService)


// libs/core pacakge 
lazy val core = (project in file("libs/core")).
  settings(commonSettings: _*).
  settings(
    name := "core"
  )


lazy val dictionaryService = (project in file("services/dictionary")).
  settings(scalaCanSetting: _*).
  settings(
    name := "Dictionary Service"
  ).
  dependsOn(core)


// revolver for "root" project
Revolver.settings
