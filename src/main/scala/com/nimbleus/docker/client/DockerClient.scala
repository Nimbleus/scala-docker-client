package com.nimbleus.docker.client

import akka.actor.ActorSystem
import akka.util.Timeout
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
import scala.concurrent.duration._

object DockerClient {
  private val AUTH_CONFIG_HEADER = "X-Registry-Auth"
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

  def createContainer(serverUrl: String, authConfig: AuthConfig, containerConfig: CreateConfig, name: Option[String])(implicit system: ActorSystem) : Future[CreateContainerResponse] = {
    import system.dispatcher
    implicit val requestTimeout = Timeout(120 seconds)
    val result = Promise[CreateContainerResponse]
    val authEncoded = authConfig.base64encode

    //extract the version tag if it exists
    val versionStart = containerConfig.Image.lastIndexOf(":")
    val version : Option[String] = if (versionStart != -1) { Some("&tag=" + containerConfig.Image.substring(versionStart + 1)) } else { None }
    val image = if (versionStart == -1) { containerConfig.Image } else { containerConfig.Image.substring(0, versionStart) }

    val pipeline = sendReceive
    pipeline(Post(serverUrl + "/images/create?fromImage=" + image + version.getOrElse("")) ~> addHeader(AUTH_CONFIG_HEADER, authEncoded)) onComplete {
      case Success(response: HttpResponse) => {
        response.status.intValue match {
          case 200 => {
            // the image was present or downloaded
            val createPipe = sendReceive ~> unmarshal[CreateContainerResponse]
            var postUrl = "/containers/create"
            if (name.isDefined) {
              postUrl = postUrl + "?name=" + name.get
            }
            createPipe(Post(serverUrl + postUrl, containerConfig)) onComplete {
              case Success(response: CreateContainerResponse) => {
                result.success(response)
              }
              case Failure(e1) => {
                result.failure(e1)
              }
            }
          }
          case 500 => {
            result.failure(new Exception("Failed to provision image : " + image))
          }
        }
      }
      case Failure(e2) => {
        result.failure(e2)
      }
    }
    result.future
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

  def createImage(serverUrl: String, image: String, tag: String, authConfig: AuthConfig)(implicit system: ActorSystem) : Future[Boolean] = {
    import system.dispatcher
    val result = Promise[Boolean]
    val authEncoded = authConfig.base64encode
    val pipeline = sendReceive
    pipeline(Post(serverUrl + "/images/create?fromImage=" + image + "&tag=" + tag) ~> addHeader(AUTH_CONFIG_HEADER, authEncoded)) onComplete {
      case Success(response: HttpResponse) => {
        response.status.intValue match {
          case 200 => {
            result.success(true)
          }
          case 500 => {
            result.success(false)
          }
        }
      }
      case Failure(e) => {
        result.failure(e)
      }
    }
    result.future // return the future
  }
}
