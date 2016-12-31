name := "scala-docker-client"

organization := "com.nimbleus"

version in ThisBuild := DockerClient.Version

scalaVersion := "2.11.5"

crossScalaVersions  := Seq("2.11.5", "2.10.0")

mainClass := Some("com.nimbleus.docker.client.Main")

// developers will only have read only access to the artifacts
// dev-ops will have full publishing rights
credentials += Credentials(
  if (Path(Path.userHome + "/.sbt/.nimbleus-artifactory-creds").exists) {
    new File(Path.userHome, ".sbt/.nimbleus-artifactory-creds")
  } else {
    new File("./.nimbleus-artifactory-creds")
  }
)

val localRelease  = "local-release"  at "https://nimbleus.jfrog.io/nimbleus/libs-release-local"
val localSnapshot = "local-snapshot" at "https://nimbleus.jfrog.io/nimbleus/libs-snapshot-local"

publishTo := {
  if (DockerClient.Version.trim.endsWith("SNAPSHOT"))
    Some(localSnapshot)
  else
    Some(localRelease)
}

publishArtifact in Test := false

resolvers += Resolvers.nimbleusSnapshots
resolvers += Resolvers.nimbleusReleases

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





