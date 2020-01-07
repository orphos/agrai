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
)
libraryDependencies in ThisBuild ++= Seq(
  "org.scalatest" %% "scalatest" % "3.1.0",
).map(_ % Test)

resourceGenerators in Compile += Def.task {
  val nativeDir = file("src") / "main" / "native"
  if (!(nativeDir / "meson.build").exists) {
    Seq()
  } else {
    val javaHome = file(System.getProperty("java.home"))
    val jdk = if (javaHome.getName == "jre") javaHome.getParentFile else javaHome
    val jre = jdk / "jre"
    val include = jdk / "include"
    val incDirs = include +: Option(include.listFiles).toSeq.flatten.filter(_.isDirectory)
    val linkDirs = Seq(jdk / "lib") ++ (if ((jre / "lib").exists) Some(jre / "lib") else None)
    val linkArgs = linkDirs.mkString("-L", ",-L", "") ++ ",-ljava"
    scala.sys.process.Process(
      Seq("meson", "build", s"-Dinc_dirs=${incDirs.mkString(",")}", s"-Dlink_args=$linkArgs", "-Dlink_depends=libjava"),
      Some(nativeDir)).!
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

lazy val root = Project(id = "jvm-benchmark", base = file("."))

