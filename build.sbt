

organization := "com.geishatokyo"

name := "typesafeconfig-scala"

version := "0.0.4-SNAPSHOT"

scalaVersion := "2.11.8"

crossScalaVersions := List("2.11.8","2.12.0")

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.1" % "provided",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)


libraryDependencies <++= (scalaVersion)(projectScalaVersion => {
  Seq("org.scala-lang" % "scala-reflect" % projectScalaVersion)
})

