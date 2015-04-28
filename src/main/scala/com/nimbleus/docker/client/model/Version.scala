/**
 * Copyright (C) 2013 Nimbleus LLC.
 */
package com.nimbleus.docker.client.model

case class Version(Version: String, Os: String, KernelVersion: String,
                   GitCommit: String, GoVersion: String, Arch: String, ApiVersion: String)

object versionHelper {
  def getErrorReason(responseCode: Int, errorDescription: String) : String = {
    responseCode match {
      case 500 => "Bad version request: "  + errorDescription
      case _ => "Unknown version error: " + errorDescription
    }
  }
}

