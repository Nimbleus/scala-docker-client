/**
 * Copyright (C) 2013 Nimbleus LLC.
 */
import sbt._
import Keys._

object DockerClient {
  val Version = "0.11.9"
}

object Versions {
  val akka         = "2.3.10"
  val sprayIo      = "1.3.2"
  val sprayJson    = "1.3.1"
  val ScalaTest    = "2.2.4"
  val softprops = "0.2.0"
}

object Resolvers {
  val typeSafe                 = "Typesafe Repo"              at "http://repo.typesafe.com/typesafe/releases/"
  val ossSonatypeReleases      = "OSS Sonatype Releases"      at "https://oss.sonatype.org/content/repositories/releases"
  val ossSonatypeSnapshots     = "OSS Sonatype Snapshots"     at "https://oss.sonatype.org/content/repositories/snapshots"
  val sprayRelease             = "spray repo"                 at "http://repo.spray.io"
  val sprayNightly             = "spray nightly repo"         at "http://nightlies.spray.io"
  val softprops                = "SoftProps"                  at "http://dl.bintray.com/content/softprops/maven"
}

object Compile {
  val akkaActor = "com.typesafe.akka" %% "akka-actor" % Versions.akka % "compile"
  val akkaKernel = "com.typesafe.akka" %% "akka-kernel" % Versions.akka % "compile"
  val sprayClient = "io.spray" %% "spray-client" % Versions.sprayIo % "compile"
  val sprayJson = "io.spray" %%  "spray-json" % Versions.sprayJson % "compile"
  val scalaTest = "org.scalatest" %% "scalatest" % Versions.ScalaTest % "compile"
  val akkaTestKit = "com.typesafe.akka" %% "akka-testkit" % Versions.akka % "compile"
  val softprops = "me.lessis" %% "base64" % Versions.softprops % "compile"
}

