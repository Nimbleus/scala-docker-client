/**
 * Copyright (C) 2013 Nimbleus LLC.
 */
package com.nimbleus.docker.client

import com.nimbleus.docker.client.model._
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}
import ExecutionContext.Implicits.global

/**
 * This represents the main entry point.
 * User: cstewart
 */
object Main extends App {
  val serverUrl : String = "http://localhost:4243"

/*
  val p1: ContainerParam = ContainerParamAll(true)
  //val p2: ContainerParam = ContainerParamLimit()

  val containerResponse = DockerClient.listContainers(serverUrl, p1)
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
/*
  val versionResponse = DockerClient.getVersion(serverUrl)
  versionResponse onComplete {
    case Success(result: Version) => {
      println("version => " + result.toString)
    }
    case Failure(error) =>
      println(error, "Couldn't retrieve version")
  }
/**/

  val p1: ContainerParam = ContainerParamAll(true)
  val containerResponse = DockerClient.listContainers(serverUrl, p1)
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
  }*/

  //val force: Boolean = false
  //println(force.toString.toLowerCase)

//d36fc3a46dfa
/*  val removeResponse = DockerClient.removeContainer(serverUrl, "550b66e0cf86", true)
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

/*
  val inspectResponse = DockerClient.inspectContainer(serverUrl, "804dfff9faf2")
  inspectResponse onComplete {
    case Success(inspectResult: InspectContainerResponse) => {
    //case Success(inspectResult: String) => {
      println(inspectResult.toString)
     // inspectResult.HostConfig.PortBindings.head.foreach ((t2) => println (t2._1 + "-->" + t2._2(0).HostIp + ":" + t2._2(0).HostPort))
    }
    case Failure(e) =>{
      println(e, "Couldn't not inspect container")
    }
  }*/

/*
  val env: List[String] = List("AWS_ACCESS_KEY=" + settings.awsKey, "AWS_SECRET_KEY=" + settings.awsSecret,
    "NACREOUS_PACKAGE=" + nodePackageName, "NACREOUS_ARCHIVE=" + nodeArchive,
    "SEED_NODES=" + formatSeedNodes(seedNodes).mkString(" "),
    "PROPS=" + formatProperties(properties).mkString(" "))
  val cmd: List[String] = List()
  // bind container to port 80 and inspect the container to get the bound port in port bindings
  // PortBindings":{"8080/tcp":[{"HostIp":"0.0.0.0","HostPort":"49153"}]
  val ports: List[String] = List("80")
*/

/*  val env: List[String] = List("AWS_ACCESS_KEY=AKIAJXWTFZM6XTENUK2Q", "AWS_SECRET_KEY=wfZ4fndWPcxujwFDF7CninBLuUDBQqkebZTQoGsr",
    "NACREOUS_PACKAGE=nacreous-sample-web-app", "NACREOUS_ARCHIVE=nacreous-sample-web-app.jar")
  val cmd: List[String] = List()
  val ports: List[String] = List()
  val exposedPort = DockerPortBinding(80)
  val config = CreateConfig("d3ff65e8d6c", ports, env, cmd, Some(Map("80/tcp" -> None)))
  //val config = CreateConfig("ubuntu", ports, env, cmd, Some(Map("80/tcp" -> exposedPort)))

  val createResponse = DockerClient.createContainer(serverUrl, config, Some("TEST-CONTAINER"))
  createResponse onComplete {
    case Success(result: CreateContainerResponse) => {
      println("created container with id => " + result.Id)

      val port = HostPort("")
      val sc = StartConfig(Some(Map("80/tcp" -> List(port))))

      val startResponse = DockerClient.startContainer(serverUrl, result.Id, sc)
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
  }*/
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
