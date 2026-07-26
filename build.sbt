enablePlugins(ScalaJSPlugin)

lazy val root = (project in file("."))
  .settings(
    inThisBuild(List(
      organization := "com.mogproject",
      scalaVersion := "2.13.18"
    )),
    name := "mog-playground",
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "1.0.0",
      "be.doeraene" %%% "scalajs-jquery" % "1.0.0",
      "com.lihaoyi" %%% "scalatags" % "0.9.1"
    ),
    ThisBuild / scalacOptions ++= Seq("-unchecked", "-deprecation"),

    packageJSDependencies / skip := false,

    jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv(
      org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv.Config().withArgs(List("--dns-result-order=ipv4first"))
    )
  )
  .dependsOn(mogFrontend)

lazy val mogFrontend = ProjectRef(uri("ssh://git@github.com/mogproject/mog-frontend.git#master"), "root")
