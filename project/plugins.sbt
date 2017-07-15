addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.16")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")

/* sbt-httpserver */
resolvers ++= Seq(
  Resolver.url("wav", url("https://dl.bintray.com/wav/maven"))(Resolver.ivyStylePatterns),
  "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases") // scalaz-stream
addSbtPlugin("wav.devtools" % "sbt-httpserver" % "0.3.1")

addSbtPlugin("com.eed3si9n" % "sbt-sh" % "0.1.0")