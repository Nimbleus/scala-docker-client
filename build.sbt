name := "scala-docker-client"

organization := "com.nimbleus"

version in ThisBuild := DockerClient.Version

scalaVersion := "2.11.5"

crossScalaVersions  := Seq("2.11.5", "2.10.0")

mainClass := Some("com.nimbleus.docker.client.Main")

resolvers ++= Seq("Nimbleus Releases" at "https://repository-nimbleus.forge.cloudbees.com/release/", "Nimbleus Snapshots" at "https://repository-nimbleus.forge.cloudbees.com/snapshot/")

credentials += Credentials(
      if (Path("/private/nimbleus/repository.credentials").exists) new File("/private/nimbleus/repository.credentials")
      else new File(Path.userHome, ".sbt/.nimbleus-credentials"))

publishTo := {
  val nimbleus = "https://repository-nimbleus.forge.cloudbees.com/"
  if (DockerClient.Version.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nimbleus + "snapshot/")
  else
    Some("releases"  at nimbleus + "release/")
}

resolvers += Resolvers.typeSafe

resolvers += Resolvers.softprops

libraryDependencies ++= Seq(
  Compile.akkaKernel,
  Compile.akkaActor,
  Compile.akkaTestKit,
  Compile.akkaCluster,
  Compile.akkaStream,
  Compile.akkaHttpExperimental,
  Compile.akkaHttpSprayJsonExperimental,
  Compile.akkaHttpCore,
  Compile.akkaHttpTestKit,
  Compile.scalaTest,
  Compile.softprops
)





