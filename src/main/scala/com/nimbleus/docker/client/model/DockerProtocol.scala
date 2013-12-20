/**
 * Copyright (C) 2013 Nimbleus LLC.
 */
package com.nimbleus.docker.client.model

import spray.json.DefaultJsonProtocol

/**
 * This object registers all models as JSON compatible thus they can be
 * serialized and de-serialized in JSON format.
 * User: cstewart
 */
object DockerProtocol extends DefaultJsonProtocol {
  implicit val versionProtocol = jsonFormat3(Version)
  implicit val portProtocol = jsonFormat3(Port)
  implicit val containerProtocol = jsonFormat9(Container.apply)
  implicit val imageProtocol = jsonFormat5(Image)
  implicit val containerProcessProtocol = jsonFormat2(ContainerProcess)
  implicit val infoProtocol = jsonFormat14(Info)
  implicit val createContainerProtocol = jsonFormat4(CreateConfig)
  implicit val createContainerResponseProtocol = jsonFormat1(CreateContainerResponse)
}

