enablePlugins(ScalaJSPlugin)

lazy val root = (project in file("."))
  .settings(
    inThisBuild(List(
      organization := "com.mogproject",
      scalaVersion := "2.13.2"
    )),
    name := "mog-playground",
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "1.0.0",
      "be.doeraene" %%% "scalajs-jquery" % "1.0.0",
      "com.lihaoyi" %%% "scalatags" % "0.9.1"
    ),
    scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation"),

    skip in packageJSDependencies := false
  )
  .dependsOn(mogFrontend)

lazy val mogFrontend = ProjectRef(uri("git://github.com/mogproject/mog-frontend.git#master"), "root")
