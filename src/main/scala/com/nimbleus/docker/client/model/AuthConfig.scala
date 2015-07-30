package com.nimbleus.docker.client.model

import spray.json._
import DefaultJsonProtocol._

object AuthConfigProtocol extends DefaultJsonProtocol {
  implicit val AuthConfigFormat = jsonFormat4(AuthConfig)
}

// THESE IMPORTS MUST BE IN SCOPE, DO NOT REMOVE
import AuthConfigProtocol._

case class AuthConfig(username: String, password: String, auth: String, email: String) {
  def base64encode : String = {
    new String(base64.Encode(this.toJson.toString))
  }
}

