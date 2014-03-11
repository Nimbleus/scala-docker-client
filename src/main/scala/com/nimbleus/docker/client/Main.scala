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

/*
  val versionResponse = DockerClient.getVersion(serverUrl)
  versionResponse onComplete {
    case Success(result: Version) => {
      println("version => " + result.toString)
    }
    case Failure(error) =>
      println(error, "Couldn't retrieve version")
  }
*/

/*
  val containerResponse = DockerClient.listContainers(serverUrl)
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
*/

//d36fc3a46dfa
/*
  val removeResponse = DockerClient.removeContainer(serverUrl, "8a8cba6a0c0c")
  removeResponse onComplete {
    case Success(removeResult: String) => {
      println(removeResult)
    }
    case Failure(e) =>{
      println(e, "Couldn't not remove container")
    }
  }*/


/*
  val killResponse = DockerClient.killContainer(serverUrl, "155696e7a916cac9af9fe8fd92dce2036084ee2486b8212cdfaf286eb76d9708")
  killResponse onComplete {
    case Success(killResult: String) => {
      println(killResult)
    }
    case Failure(e) =>{
      println(e, "Couldn't not kill container")
    }
  }
*/

  
  val inspectResponse = DockerClient.inspectContainer(serverUrl, "672bb1f6d369116d7291fff01b6e869f935d740f95a39f74015db8d2c97bdd2b")
  inspectResponse onComplete {
    case Success(inspectResult: InspectContainerResponse) => {
    //case Success(inspectResult: String) => {
      println(inspectResult.toString)
    }
    case Failure(e) =>{
      println(e, "Couldn't not inspect container")
    }
  }

/*
  val env: List[String] = List()
  val cmd: List[String] = List("/bin/sh", "-c", "while true; do echo Hello world; sleep 1; done")
  val ports: List[String] = List()

  val config = CreateConfig("ubuntu", ports, env, cmd)

  val createResponse = DockerClient.createContainer(serverUrl, config, Some("TEST-CONTAINER"))
  createResponse onComplete {
    case Success(result: CreateContainerResponse) => {
      println("created container with id => " + result.Id)
      val startResponse = DockerClient.startContainer(serverUrl, result.Id)
      startResponse onComplete {
        case Success(startResult: String) => {
          println(startResult)
        }
        case Failure(e) =>{
          println(e, "Couldn't not start container")
        }
      }
    }
    case Failure(e) =>{
      println(e, "Couldn't not create container")
    }
  }
*/
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
