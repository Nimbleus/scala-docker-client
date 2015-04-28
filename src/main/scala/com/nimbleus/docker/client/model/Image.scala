/**
 * Copyright (C) 2013 Nimbleus LLC.
 */
package com.nimbleus.docker.client.model

case class Image(Id: String, RepoTags: List[String], Created: Int, Size: Int, VirtualSize: Int)

object ImageHelper {
  def getErrorReason(responseCode: Int, errorDescription: String) : String = {
    responseCode match {
      case 500 => "Bad image request: "  + errorDescription
      case _ => "Unknown image error: " + errorDescription
    }
  }
}

