/**
 * Copyright (C) 2013 Nimbleus LLC.
 */
package com.nimbleus.docker.client.model

case class Info(OperatingSystem: String, Containers: Int, Images: Int, Driver: String,
                KernelVersion: String, NCPU: Int, MemTotal: Int, Name: String,
                ID: String, OSType: String, MemoryLimit: Boolean,
                DockerRootDir: String, ServerVersion: String, Architecture: String)
object InfoHelper {
  def getErrorReason(responseCode: Int, errorDescription: String) : String = {
    responseCode match {
      case 500 => "Bad system info request: "  + errorDescription
      case _ => "Unknown system info error: " + errorDescription
    }
  }
}

