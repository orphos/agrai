// Copyright 2019-2020 Takezoe,Tomoaki <tomoaki3478@res.ac>
//
// SPDX-License-Identifier: Apache-2.0 WITH LLVM-exception
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// As an exception, if, as a result of your compiling your source code, portions
// of this Software are embedded into an Object form of such source code, you
// may redistribute such embedded portions in such Object form without complying
// with the conditions of Sections 4(a), 4(b) and 4(d) of the License.
//
// In addition, if you combine or link compiled forms of this Software with
// software that is licensed under the GPLv2 ("Combined Software") and if a
// court of competent jurisdiction determines that the patent provision (Section
// 3), the indemnity provision (Section 9) or other Section of the License
// conflicts with the conditions of the GPLv2, you may retroactively and
// prospectively choose to deem waived or otherwise exclude such Section(s) of
// the License, but only in their entirety and only with respect to the Combined
// Software.

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

