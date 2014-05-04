package com.nimbleus.docker.client

/**
 * Copyright (C) 2013 Nimbleus LLC.
 */
import akka.actor.ActorSystem
import com.nimbleus.docker.client.model._
import com.nimbleus.docker.client.model.Container
import com.nimbleus.docker.client.model.Image
import com.nimbleus.docker.client.model.Version
import scala.concurrent.{Promise, Future}
import spray.client.pipelining._
import spray.httpx.SprayJsonSupport
import spray.json._
import DefaultJsonProtocol._
import com.nimbleus.docker.client.model.Info
import com.nimbleus.docker.client.model.ContainerProcess
import com.nimbleus.docker.client.model.Version
import com.nimbleus.docker.client.model.Image
import scala.util.{Failure, Success}
import spray.http.{StatusCode, HttpResponse}

/**
 * This object exposes functions that call external Docker daemons
 * through the Docker Remote API
 * User: cstewart
 */
object DockerClient {
  // TODO use implicit actor system of the calling class
  implicit val system = ActorSystem("DockerClient")
  import system.dispatcher // execution context for futures below
  import DockerProtocol._  // this is required to be in scope
  import SprayJsonSupport._ // this is required to be in scope

  // SYSTEM CALLS
  def getVersion(serverUrl: String) : Future[Version] = {
    val pipeline = sendReceive ~> unmarshal[Version]
    pipeline(Get(serverUrl + "/version"))
  }
  def getInfo(serverUrl: String) : Future[Info] = {
    val pipeline = sendReceive ~> unmarshal[Info]
    pipeline(Get(serverUrl + "/info"))
  }

  // CONTAINER CALLS
  def createContainer(serverUrl: String, containerConfig: CreateConfig, name: Option[String]) : Future[CreateContainerResponse] = {
    val pipe = sendReceive ~> unmarshal[CreateContainerResponse]
    var postUrl = "/containers/create"
    if (name.isDefined) {
      postUrl = postUrl + "?name=" + name.get
    }
    pipe(Post(serverUrl + postUrl, containerConfig))
  }

  def startContainer(serverUrl: String, containerId: String, startConfig: StartConfig) : Future[String] = {
    val result = Promise[String]
    val pipeline = sendReceive
    val startResponse = pipeline(Post(serverUrl + "/containers/" + containerId + "/start", startConfig))
    startResponse onComplete {
      case Success(response: HttpResponse) => {
        response.status.intValue match {
          case 204 => {result.success("started container with id => " + containerId)}
          case 404 => {result.failure(new Exception("no such container"))}
          case 500 => {result.failure(new Exception("server error"))}
        }
      }
      case Failure(e) =>{
        result.failure(e)
      }
    }
    result.future // return the future
  }

  def killContainer(serverUrl: String, containerId: String) : Future[String] = {
    val result = Promise[String]
    val pipeline = sendReceive
    val startResponse = pipeline(Post(serverUrl + "/containers/" + containerId + "/kill"))
    startResponse onComplete {
      case Success(response: HttpResponse) => {
        response.status.intValue match {
          case 204 => {result.success("killed container with id => " + containerId)}
          case 404 => {result.failure(new Exception("no such container"))}
          case 500 => {result.failure(new Exception("server error"))}
        }
      }
      case Failure(e) =>{
        result.failure(e)
      }
    }
    result.future // return the future
  }

  def removeContainer(serverUrl: String, containerId: String, force: Boolean = false) : Future[String] = {
    val result = Promise[String]
    val pipeline = sendReceive
    val startResponse = pipeline(Delete(serverUrl + "/containers/" + containerId + "?v=1&force="+ force.toString.toLowerCase()))
    startResponse onComplete {
      case Success(response: HttpResponse) => {
        response.status.intValue match {
          case 204 => {result.success("removed container with id => " + containerId)}
          case 400 => {result.failure(new Exception("bad parameter"))}
          case 404 => {result.failure(new Exception("no such container"))}
          case 500 => {result.failure(new Exception("server error"))}
        }
      }
      case Failure(e) =>{
        result.failure(e)
      }
    }
    result.future // return the future
  }

  def inspectContainer(serverUrl: String, containerId: String) : Future[InspectContainerResponse] = {
    val pipeline = sendReceive ~> unmarshal[InspectContainerResponse]
    pipeline(Get(serverUrl + "/containers/" + containerId + "/json"))
  }

  def listContainers(serverUrl: String, args:ContainerParam*) : Future[List[Container]] = {
    // parse the parameters if they exist
    val url = if (args.size > 0){
      serverUrl + "/containers/json?" + args.map((i: ContainerParam) => i.field + "=" + (if(i.value.nonEmpty){i.value.get}else{""})).mkString("&")
    } else {serverUrl + "/containers/json"}
    println(url)
    // parse the parameter list
    val pipeline = sendReceive ~> unmarshal[List[Container]]
    pipeline(Get(url))
  }

  def getContainerProcesses(serverUrl: String, processId: String) : Future[ContainerProcess] = {
    val pipeline = sendReceive ~> unmarshal[ContainerProcess]
    pipeline(Get(serverUrl + "/containers/" + processId + "/top"))
  }

  def stopContainer(serverUrl: String, containerId: String, waitSecs: Int) : Future[HttpResponse] = {
    val pipeline = sendReceive
    pipeline(Post(serverUrl + "/containers/" + containerId + "/stop?t=" + waitSecs))
  }

  def restartContainer(serverUrl: String, containerId: String, waitSecs: Int) : Future[HttpResponse] = {
    val pipeline = sendReceive
    pipeline(Post(serverUrl + "/containers/" + containerId + "/restart?t=" + waitSecs))
  }

  // IMAGE CALLS
  def getImages(serverUrl: String) : Future[List[Image]] = {
    val pipeline = sendReceive ~> unmarshal[List[Image]]
    pipeline(Get(serverUrl + "/images/json"))
  }
  def shutdown() = {system.shutdown()}
}
