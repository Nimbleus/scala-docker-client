name := "scala-docker-client"

version in ThisBuild := DockerClient.Version

scalaVersion := "2.10.2"

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

resolvers += Resolvers.sprayRelease

resolvers += Resolvers.sprayNightly

resolvers += Resolvers.typeSafe

libraryDependencies ++= Seq(
  Compile.sprayClient,
  Compile.sprayJson,
  Compile.akkaKernel,
  Compile.akkaActor,
  Compile.scalaTest
)





