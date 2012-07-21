scalaVersion := "2.9.2"

resolvers ++= Seq(
  "snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "releases"  at "http://oss.sonatype.org/content/repositories/releases"
)

libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.0-SNAPSHOT"

libraryDependencies += "org.scalaz" %% "scalaz-effect" % "7.0-SNAPSHOT"

libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.10.0" % "test"

libraryDependencies += "org.specs2" %% "specs2" % "1.11" % "test"
