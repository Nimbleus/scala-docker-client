/**
 * Copyright (C) 2013 Nimbleus LLC.
 */
package com.nimbleus.docker.client

import com.nimbleus.docker.client.model._
import com.nimbleus.docker.client.model.Container
import com.nimbleus.docker.client.model.Version
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}
import spray.httpx.UnsuccessfulResponseException
import ExecutionContext.Implicits.global

/**
 * This represents the main entry point.
 * User: cstewart
 */
object Main extends App {
  val serverUrl : String = "http://localhost:4243"

  val createResponse = DockerClient.createContainer(serverUrl, CreateConfig("ubuntu", None, None, List("touch", "/test")))
  createResponse onComplete {
    case Success(result: CreateContainerResponse) => {
      println("created conatiner with id => " + result.Id)
    }
    case Failure(e) =>{
      println(e, "Couldn't not create container")
    }
  }
/*
  val versionResponse = DockerClient.getVersion(serverUrl)
  versionResponse onComplete {
    case Success(result: Version) => {
      println("version => " + result.toString)
    }
    case Failure(error) =>
      println(error, "Couldn't retrieve version")
  }

  val infoResponse = DockerClient.getInfo(serverUrl)
  infoResponse onComplete {
    case Success(result: Info) => {
      println("system info => " + result.toString)
    }
    case Failure(error: UnsuccessfulResponseException) => {
      println(InfoHelper.getErrorReason(error.response.status.intValue, error.response.message.toString))
    }
    case Failure(e) =>{
      println(e, "Couldn't get system info")
    }
  }

  val p1: ContainerParam = ContainerParamAll(true)
  val p2: ContainerParam = ContainerParamLimit()

  val containerResponse = DockerClient.getContainers(serverUrl, p1, p2)
  containerResponse onComplete {
    case Success(result: List[Container]) => {
      println("containers => " + result.toString)
      for(c <- result){
        val containerProcessorResponse = DockerClient.getContainerProcesses(serverUrl, c.Id)
        containerProcessorResponse onComplete {
          case Success(result: ContainerProcess) => {
            println("container processes => " + result.toString)
          }
          case Failure(error: UnsuccessfulResponseException) => {
            println(ContainerProcessHelper.getErrorReason(error.response.status.intValue, error.response.message.toString))
          }
          case Failure(e) => {
            println(e, "Couldn't list container processes")
          }
        }
      }
    }
    case Failure(error: UnsuccessfulResponseException) => {
      println(Container.getErrorReason(error.response.status.intValue, error.response.message.toString))
    }
    case Failure(e) =>{
      println(e, "Couldn't list containers")
    }
  }

  val imageResponse = DockerClient.getImages(serverUrl)
  imageResponse onComplete {
    case Success(result: List[Image]) => {
      println("images => " + result.toString)
    }
    case Failure(error: UnsuccessfulResponseException) => {
      println(ImageHelper.getErrorReason(error.response.status.intValue, error.response.message.toString))
    }
    case Failure(e) =>{
      println(e, "Couldn't list images")
    }
  }
*/

}
