/**
 * Copyright (C) 2013 Nimbleus LLC.
 */
import sbt._
import Keys._

object DockerClient {
  val Version = "0.1.0-SNAPSHOT"
}

object Versions {
  val akka         = "2.2.3"
  val sprayIo      = "1.2-20131004"
  val sprayJson    = "1.2.3"
  val ScalaTest    = "1.9.1"
}

object Resolvers {
  val typeSafe                 = "Typesafe Repo"              at "http://repo.typesafe.com/typesafe/releases/"
  val ossSonatypeReleases      = "OSS Sonatype Releases"      at "https://oss.sonatype.org/content/repositories/releases"
  val ossSonatypeSnapshots     = "OSS Sonatype Snapshots"     at "https://oss.sonatype.org/content/repositories/snapshots"
  val sprayRelease             = "spray repo"                 at "http://repo.spray.io"
  val sprayNightly             = "spray nightly repo"         at "http://nightlies.spray.io"
}

object Compile {
  val akkaActor = "com.typesafe.akka" %% "akka-actor" % Versions.akka % "compile"
  val akkaKernel = "com.typesafe.akka" %% "akka-kernel" % Versions.akka % "compile"
  val sprayClient = "io.spray" % "spray-client" % Versions.sprayIo % "compile"
  val sprayJson = "io.spray" %%  "spray-json" % Versions.sprayJson % "compile"
  val scalaTest = "org.scalatest" %% "scalatest" % Versions.ScalaTest % "compile"
}

