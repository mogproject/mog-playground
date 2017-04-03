import wav.devtools.sbt.httpserver.{SbtHttpServerPlugin, FileServer}
import SbtHttpServerPlugin.autoImport._

enablePlugins(SbtHttpServerPlugin)
addHttpServices(
  _ += FileServer.service("test", (resourceDirectories in Test).value)
)

enablePlugins(ScalaJSPlugin)

lazy val root = (project in file("."))
  .settings(
    inThisBuild(List(
      organization := "com.mogproject",
      scalaVersion := "2.12.0"
    )),
    name := "mog-playground",
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.1",
      "be.doeraene" %%% "scalajs-jquery" % "0.9.1",
      "com.lihaoyi" %%% "scalatags" % "0.6.2",
      "org.scalatest" %%% "scalatest" % "3.0.1" % Test,
      "org.scalacheck" %%% "scalacheck" % "1.13.4" % Test
    ),
    scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation"),

    skip in packageJSDependencies := false,

    jsDependencies ++= Seq(
      RuntimeDOM,
      ProvidedJS / "assets/js/ecl_new.js"
    )
  )
  .dependsOn(mogCore)

lazy val mogCore = ProjectRef(uri("git://github.com/mogproject/mog-core-scala.git#master"), "mogCoreJS")
