/**
 * Copyright (C) 2013 Nimbleus LLC.
 */
package com.nimbleus.docker.client.model

/**
 * This model represents a docker system info.
 * User: cstewart
 */
case class Info(Debug: Boolean, Containers: Int, Images: Int, Driver: String, DriverStatus: List[List[String]],
                NFd: Int, NGoroutines: Int, MemoryLimit: Boolean, SwapLimit: Boolean, IPv4Forwarding: Boolean,
                LXCVersion: String, NEventsListener: Int, KernelVersion: String, IndexServerAddress: String)
object InfoHelper {
  def getErrorReason(responseCode: Int, errorDescription: String) : String = {
    responseCode match {
      case 500 => "Bad system info request: "  + errorDescription
      case _ => "Unknown system info error: " + errorDescription
    }
  }
}
