/**
 * Copyright (C) 2013 Nimbleus LLC.
 */
package com.nimbleus.docker.client.model

/**
 * This model represents a the Docker versions.
 * User: cstewart
 */
case class Version(Version: String, GitCommit: String, GoVersion: String)

object versionHelper {
  def getErrorReason(responseCode: Int, errorDescription: String) : String = {
    responseCode match {
      case 500 => "Bad version request: "  + errorDescription
      case _ => "Unknown version error: " + errorDescription
    }
  }
}