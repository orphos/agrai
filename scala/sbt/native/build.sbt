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
  "commons-io" % "commons-io" % "2.6",
  "org.apache.commons" % "commons-lang3" % "3.9",
  "com.github.jnr" % "jnr-ffi" % "2.1.11"
)
libraryDependencies in ThisBuild ++= Seq(
  "org.scalatest" %% "scalatest" % "3.1.0",
).map(_ % Test)

resourceGenerators in Compile += Def.task {
  val nativeDir = file("src") / "main" / "native"
  if (!(nativeDir / "meson.build").exists) {
    Seq()
  } else {
    scala.sys.process.Process(Seq("meson", "build"), Some(nativeDir)).!
    scala.sys.process.Process(Seq("ninja"), Some(nativeDir / "build")).!
    val Ext = """.*\.(?:so|dll|dylib)[.0-9]*""".r
    val libs = (nativeDir / "build").list().flatMap {
      case name @ Ext() => Some(nativeDir / "build" / name)
      case _ => None
    }
    for (lib <- libs) yield {
      val target = (resourceManaged in Compile).value / lib.name
      IO.copyFile(lib, target)
      target
    }
  } : Seq[File]
}.taskValue

lazy val root = Project(id = "sbt-native-example", base = file("."))

