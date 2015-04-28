package com.nimbleus.docker.client

import akka.actor.ActorSystem
import com.nimbleus.docker.client.model._
import com.nimbleus.docker.client.model.Container
import scala.concurrent.{Promise, Future}
import spray.client.pipelining._
import spray.httpx.SprayJsonSupport
import com.nimbleus.docker.client.model.Info
import com.nimbleus.docker.client.model.Version
import com.nimbleus.docker.client.model.Image
import scala.util.{Failure, Success}
import spray.http.HttpResponse

object DockerClient {

  import DockerProtocol._  // this is required to be in scope
  import SprayJsonSupport._ // this is required to be in scope

  // SYSTEM CALLS
  def getVersion(serverUrl: String)(implicit system: ActorSystem) : Future[Version] = {
    import system.dispatcher
    val pipeline = sendReceive ~> unmarshal[Version]
    pipeline(Get(serverUrl + "/version"))
  }
  def getInfo(serverUrl: String)(implicit system: ActorSystem) : Future[Info] = {
    import system.dispatcher
    val pipeline = sendReceive ~> unmarshal[Info]
    pipeline(Get(serverUrl + "/info"))
  }

  // CONTAINER CALLS
  def createContainer(serverUrl: String, containerConfig: CreateConfig, name: Option[String])(implicit system: ActorSystem) : Future[CreateContainerResponse] = {
    import system.dispatcher
    val pipe = sendReceive ~> unmarshal[CreateContainerResponse]
    var postUrl = "/containers/create"
    if (name.isDefined) {
      postUrl = postUrl + "?name=" + name.get
    }
    pipe(Post(serverUrl + postUrl, containerConfig))
  }

  def startContainer(serverUrl: String, containerId: String)(implicit system: ActorSystem) : Future[String] = {
    import system.dispatcher
    val result = Promise[String]
    val pipeline = sendReceive
    val startResponse = pipeline(Post(serverUrl + "/containers/" + containerId + "/start"))
    startResponse onComplete {
      case Success(response: HttpResponse) => {
        response.status.intValue match {
          case 204 => {result.success("started container with id => " + containerId)}
          case 304 => {result.failure(new Exception("container already started"))}
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

  def killContainer(serverUrl: String, containerId: String)(implicit system: ActorSystem) : Future[String] = {
    import system.dispatcher
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

  def removeContainer(serverUrl: String, containerId: String, force: Boolean = false)(implicit system: ActorSystem) : Future[String] = {
    import system.dispatcher
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

  def stopContainer(serverUrl: String, containerId: String, waitSecs: Int = 0)(implicit system: ActorSystem) : Future[String] = {
    import system.dispatcher
    val result = Promise[String]
    val pipeline = sendReceive
    val stopResponse = pipeline(Post(serverUrl + "/containers/" + containerId + "/stop?t=" + waitSecs))
    stopResponse onComplete {
      case Success(response: HttpResponse) => {
        response.status.intValue match {
          case 204 => {result.success("stopped container with id => " + containerId)}
          case 304 => {result.failure(new Exception("container already stopped"))}
          case 404 => {result.success("no such container")}
          case 500 => {result.failure(new Exception("server error"))}
        }
      }
      case Failure(e) =>{
        result.failure(e)
      }
    }
    result.future // return the future
  }

  def restartContainer(serverUrl: String, containerId: String, waitSecs: Int = 0)(implicit system: ActorSystem) : Future[String] = {
    import system.dispatcher
    val result = Promise[String]
    val pipeline = sendReceive
    val stopResponse = pipeline(Post(serverUrl + "/containers/" + containerId + "/restart?t=" + waitSecs))
    stopResponse onComplete {
      case Success(response: HttpResponse) => {
        response.status.intValue match {
          case 204 => {result.success("restarted container with id => " + containerId)}
          case 404 => {result.success("no such container")}
          case 500 => {result.failure(new Exception("server error"))}
        }
      }
      case Failure(e) =>{
        result.failure(e)
      }
    }
    result.future // return the future
  }

  def listContainers(serverUrl: String, args:ContainerParam *)(implicit system: ActorSystem) : Future[List[Container]] = {
    import system.dispatcher
    // parse the parameters if they exist
    val url = if (args.size > 0){
      serverUrl + "/containers/json?" + args.map((i: ContainerParam) => i.field + "=" + (if(i.value.nonEmpty){i.value.get}else{""})).mkString("&")
    } else {serverUrl + "/containers/json"}
    // parse the parameter list
    val pipeline = sendReceive ~> unmarshal[List[Container]]
    pipeline(Get(url))
  }

  def inspectContainer(serverUrl: String, containerId: String)(implicit system: ActorSystem) : Future[InspectContainerResponse] = {
    import system.dispatcher
    val pipeline = sendReceive ~> unmarshal[InspectContainerResponse]
    pipeline(Get(serverUrl + "/containers/" + containerId + "/json"))
  }

  // IMAGE CALLS
  def getImages(serverUrl: String)(implicit system: ActorSystem) : Future[List[Image]] = {
    import system.dispatcher
    val pipeline = sendReceive ~> unmarshal[List[Image]]
    pipeline(Get(serverUrl + "/images/json"))
  }
}
