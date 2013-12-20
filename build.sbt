name := "scala-docker-client"

version in ThisBuild := DockerClient.Version

scalaVersion := "2.10.2"

mainClass := Some("com.nimbleus.docker.client.Main")

resolvers ++= Seq("Nimbleus Releases" at "https://repository-nimbleus.forge.cloudbees.com/release/", "Nimbleus Snapshots" at "https://repository-nimbleus.forge.cloudbees.com/snapshot/")

credentials += Credentials(new File(Path.userHome, ".sbt/.viridity-credentials"))

resolvers += Resolvers.spray

resolvers += Resolvers.typeSafe

libraryDependencies ++= Seq(
  Compile.sprayClient,
  Compile.sprayJson,
  Compile.akkaKernel,
  Compile.akkaActor,
  Compile.scalaTest
)





