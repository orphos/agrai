import sbt._
import Keys._

scalaVersion in ThisBuild := "2.13.1"
conflictWarning in ThisBuild := ConflictWarning("global", Level.Error, failOnConflict = true)

scalacOptions in ThisBuild ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xlint",
)

scalafmtOnCompile in ThisBuild := true

javaOptions in Test ++= Seq(
  "-Xms1g",
  "-Xmx4g"
)

fork := true

libraryDependencies in ThisBuild ++= Seq(
  "org.bouncycastle" % "bcprov-jdk15on" % "1.64"
)
libraryDependencies in ThisBuild ++= Seq(
  "org.scalatest" %% "scalatest" % "3.1.0",
).map(_ % Test)

lazy val root = Project(id = "crypto-speed", base = file("."))

