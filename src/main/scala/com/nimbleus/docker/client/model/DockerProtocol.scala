/**
 * Copyright (C) 2013 Nimbleus LLC.
 */
package com.nimbleus.docker.client.model

import spray.json.DefaultJsonProtocol

object DockerProtocol extends DefaultJsonProtocol {
  implicit val versionProtocol = jsonFormat7(Version)
  implicit val portProtocol = jsonFormat3(Port)
  implicit val port2Protocol = jsonFormat2(Port2)
  implicit val containerProtocol = jsonFormat8(Container.apply)
  implicit val imageProtocol = jsonFormat5(Image)
  implicit val containerProcessProtocol = jsonFormat2(ContainerProcess)
  implicit val hostPortProtocol = jsonFormat1(HostPort)
  implicit val infoProtocol = jsonFormat15(Info)
  implicit val dockerPortBindingProtocol = jsonFormat4(DockerPortBinding)
  implicit val startConfigProtocol = jsonFormat1(StartConfig)
  implicit val createContainerProtocol = jsonFormat5(CreateConfig)
  implicit val createContainerResponseProtocol = jsonFormat1(CreateContainerResponse)
  implicit val createContainerConfigProtocol = jsonFormat15(ContainerConfig)
  implicit val createContainerLxcConfProtocol = jsonFormat2(ContainerLxcConf)
  implicit val createContainerStateProtocol = jsonFormat6(ContainerState)
  implicit val createContainerNetworkSettingsProtocol = jsonFormat6(ContainerNetworkSettings)
  implicit val createContainerHostConfigProtocol = jsonFormat7(ContainerHostConfig)
  implicit val createInspectContainerResponseProtocol = jsonFormat15(InspectContainerResponse.apply)
  implicit val weaveContainerProtocol = jsonFormat3(WeaveContainer.apply)
}



