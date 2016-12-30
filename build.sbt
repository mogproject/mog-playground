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
      "com.lihaoyi" %%% "scalatags" % "0.6.2"
    ),
    scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation"),
    jsDependencies += RuntimeDOM
  )
  .dependsOn(mogCore)

lazy val mogCore = ProjectRef(uri("git://github.com/mogproject/mog-core-scala.git#master"), "mogCoreJS")

