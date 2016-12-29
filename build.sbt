enablePlugins(ScalaJSPlugin)

lazy val root = (project in file("."))
  .settings(
    inThisBuild(List(
      organization := "com.mogproject",
      scalaVersion := "2.12.0"
    )),
    name := "mog-playground",
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.1"
    ),
    scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation")
  )
  .dependsOn(mogCore)

lazy val mogCore = ProjectRef(uri("git://github.com/mogproject/mog-core-scala.git#master"), "mogCoreJS")

