name := """scmetrics"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  jdbc,
  evolutions,
  cache,
  ws
)

libraryDependencies += "com.typesafe.play" %% "anorm" % "2.4.0"

libraryDependencies += "org.mindrot" % "jbcrypt" % "0.3m"


