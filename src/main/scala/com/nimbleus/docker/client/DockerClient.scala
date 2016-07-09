package com.nimbleus.docker.client

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import com.nimbleus.docker.client.model._
import spray.json._
import scala.concurrent.{Future, Promise}
import scala.sys.process._

class DockerClient(server: String, port: Option[Int], authConfig: AuthConfig)(implicit system: ActorSystem, materializer: ActorMaterializer) {
  import com.nimbleus.docker.client.model.DockerProtocol._
  import system.dispatcher

  // constants
  private val SERVER_HTTP = ("http://" + server + (if (port.isDefined) {":" + port.get} else {""})).trim
  private val AUTH_CONFIG_HEADER = "X-Registry-Auth"
  private val WEAVE_PS = "weave ps"
  private val TAG_VERSION = "&tag="
  private val URL_VERSION = "/version"
  private val URL_DOWNLOAD = "/images/create?fromImage="
  private val URL_INFO = "/info"
  private val URL_IMAGES = "/images"
  private val URL_IMAGES_JSON = "/images/json"
  private val URL_JSON = "/json"
  private val URL_CONTAINER_CREATE = "/containers/create"
  private val URL_CONTAINER = "/containers"
  private val URL_CONTAINER_JSON = "/containers/json"
  private val URL_CONTAINER_START = "/start"
  private val URL_CONTAINER_STOP = "/stop"
  private val URL_CONTAINER_RESTART = "/restart"
  private val URL_CONTAINER_KILL = "/kill"
  private val URL_CONTAINER_REMOVE = "/remove"
  private val URL_CONTAINER_PAUSE = "/pause"
  private val URL_CONTAINER_UNPAUSE = "/unpause"

  // error messages
  private val ERROR_UNKNOWN = "unknown response"
  private val ERROR_SERVER = "server error"
  private val ERROR_BAD_PARAMETER = "bad parameter"
  private val ERROR_NO_SUCH_CONTAINER = "no such container"
  private val ERROR_CAN_NOT_ATTACH_CONTAINER = "impossible to attach (container not running)"
  private val ERROR_COMMAND_NOT_FOUND = "command not found"
  private val ERROR_CONTAINER_ALREADY_STARTED = "container already started"
  private val ERROR_CONTAINER_ALREADY_STOPPED = "container already stopped"

  // local variables
  private val authEncoded = authConfig.base64encode

  // helper to build connections
  private def buildConnectionFlow: Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]] = Http().outgoingConnection(server, port.getOrElse(80))

  // SYSTEM CALLS
  def listWeaveContainers(implicit system: ActorSystem) : Future[List[WeaveContainer]] = {
    val result = Promise[List[WeaveContainer]]
    try {
      val data = WEAVE_PS !!

      //8c3ad03c30d3 5a:21:3d:fb:b2:a2 10.2.0.1/16
      if (data.length > 0) {
        if (!data.contains(ERROR_COMMAND_NOT_FOUND)) {
          val dataArr = data.split("\n")
          result.success(WeaveContainer.parse(dataArr))
        } else {
          result.success(List.empty[WeaveContainer])
        }
      } else {
        result.success(List.empty[WeaveContainer])
      }
    }
    catch {
      case e: java.io.IOException => { result.failure(e) }
    }
    result.future // return the future
  }

  def getVersion : Future[Version] = {
    val result = Promise[Version]
    Source.single(HttpRequest(GET, uri = SERVER_HTTP + URL_VERSION).withHeaders(
      RawHeader(AUTH_CONFIG_HEADER, authEncoded)))
      .via(buildConnectionFlow)
      .runWith(Sink.head).map { response =>
      response.status.intValue match {
        case 200 => result.completeWith(Unmarshal(response.entity).to[Version])
        case 500 => result.failure(new Exception(ERROR_SERVER))
        case _ => result.failure(new Exception(ERROR_UNKNOWN))
      }
    }
    result.future
  }

  def getInfo : Future[Info] = {
    val result = Promise[Info]
    Source.single(HttpRequest(GET, uri = SERVER_HTTP + URL_INFO).withHeaders(
      RawHeader(AUTH_CONFIG_HEADER, authEncoded)))
      .via(buildConnectionFlow).runWith(Sink.head).map { response =>
      response.status.intValue match {
        case 200 => result.completeWith(Unmarshal(response.entity).to[Info])
        case 500 => result.failure(new Exception(ERROR_SERVER))
        case _ => result.failure(new Exception(ERROR_UNKNOWN))
      }
    }
    result.future
  }

  // IMAGE CALLS
  def imageExist(image: String, version: Option[String]) : Future[Boolean] = {
    val result = Promise[Boolean]
    val target = (image + (if (version.isDefined) {":" + version.get} else {""})).trim
    val url = SERVER_HTTP + URL_IMAGES + "/" + target  + URL_JSON
    Source.single(HttpRequest(GET, uri = url).withHeaders(
      RawHeader(AUTH_CONFIG_HEADER, authEncoded)))
      .via(buildConnectionFlow).runWith(Sink.head).map { response =>
      response.status.intValue match {
        case 200 => result.success(true)
        case 404 => result.success(false)
        case 500 => result.failure(new Exception(ERROR_SERVER))
        case _ => result.failure(new Exception(ERROR_UNKNOWN))
      }
    }
    result.future
  }

  /**
   * Download target image from the dockerhub repository.
   *
   * @param image
   * @param version None for all image tags; Latest; or version
   * @return
   */
  def downloadImage(image: String, version: Option[String]) : Future[Boolean] = {
    val result = Promise[Boolean]
    val target = (image + (if (version.isDefined) { TAG_VERSION + version.get } else {""})).trim
    Source.single(HttpRequest(POST, uri = (URL_DOWNLOAD + target)).withHeaders(
      RawHeader(AUTH_CONFIG_HEADER, authEncoded)))
      .via(buildConnectionFlow).runWith(Sink.head).flatMap { response =>
      response.entity.dataBytes.runForeach { chunk =>
        println("-----")
        println(chunk.utf8String)
      } map { done => {
          result.success(true)
        }
      }
    }
    result.future
  }

  def getImages : Future[List[Image]] = {
    val result = Promise[List[Image]]
    Source.single(HttpRequest(GET, uri = SERVER_HTTP + URL_IMAGES_JSON).withHeaders(
      RawHeader(AUTH_CONFIG_HEADER, authEncoded)))
      .via(buildConnectionFlow).runWith(Sink.head).map { response =>
      response.status.intValue match {
        case 200 => result.completeWith(Unmarshal(response.entity).to[List[Image]])
        case 500 => result.failure(new Exception(ERROR_SERVER))
        case _ => result.failure(new Exception(ERROR_UNKNOWN))
      }
    }
    result.future
  }

  // CONTAINERS
  def removeContainer(containerId: String, force: Boolean = false) : Future[String] = {
    val result = Promise[String]
    Source.single(HttpRequest(DELETE, uri = SERVER_HTTP + URL_CONTAINER + "/" + containerId + "?v=1&force="+ force.toString.toLowerCase).withHeaders(
      RawHeader(AUTH_CONFIG_HEADER, authEncoded)))
      .via(buildConnectionFlow).runWith(Sink.head).map { response =>
      response.status.intValue match {
        case 204 => result.success(containerId)
        case 400 => result.failure(new Exception(ERROR_BAD_PARAMETER))
        case 404 => result.failure(new Exception(ERROR_NO_SUCH_CONTAINER))
        case 500 => {
          result.failure(new Exception(ERROR_SERVER))
        }
        case _ => result.failure(new Exception(ERROR_UNKNOWN))
      }
    }
    result.future // return the future
  }

  def createContainer(containerConfig: CreateConfig, name: Option[String]) : Future[CreateContainerResponse] = {
    val result = Promise[CreateContainerResponse]
    //extract the version tag if it exists
    val versionStart = containerConfig.Image.lastIndexOf(":")
    val version : Option[String] = if (versionStart != -1) { Some("&tag=" + containerConfig.Image.substring(versionStart + 1)) } else { None }
    val image = if (versionStart == -1) { containerConfig.Image } else { containerConfig.Image.substring(0, versionStart) }
    val postUrl = (SERVER_HTTP + URL_CONTAINER_CREATE + (if (name.isDefined) { "?name=" + name.get } else { "" })).trim
    Source.single(HttpRequest(POST, uri = postUrl, entity = HttpEntity(MediaTypes.`application/json`, containerConfig.toJson.prettyPrint)).withHeaders(
      RawHeader(AUTH_CONFIG_HEADER, authEncoded)))
      .via(buildConnectionFlow).runWith(Sink.head).map { response =>
      response.status.intValue match {
        case 201 => {
          result.completeWith(Unmarshal(response.entity).to[CreateContainerResponse])
        } //201 – no error
        case 400 => {
          result.failure(new Exception(ERROR_BAD_PARAMETER))
        } //400 – bad parameter
        case 404 => {
          result.failure(new Exception(ERROR_NO_SUCH_CONTAINER))
        } //404 – no such container
        case 406 => {
          result.failure(new Exception(ERROR_CAN_NOT_ATTACH_CONTAINER))
        } //406 – impossible to attach (container not running)
        case 500 => {
          result.failure(new Exception(ERROR_SERVER))
        } //500 – server error
        case _ => {
          result.failure(new Exception(ERROR_UNKNOWN))
        }
      }
    }
    result.future
  }

  def startContainer(containerId: String) : Future[String] = {
    val result = Promise[String]
    Source.single(HttpRequest(POST, uri = SERVER_HTTP + URL_CONTAINER + "/" + containerId + URL_CONTAINER_START).withHeaders(
      RawHeader(AUTH_CONFIG_HEADER, authEncoded)))
      .via(buildConnectionFlow).runWith(Sink.head).map { response =>
      response.status.intValue match {
        case 204 => result.success(containerId)
        case 304 => result.failure(new Exception(ERROR_CONTAINER_ALREADY_STARTED ))
        case 404 => result.failure(new Exception(ERROR_NO_SUCH_CONTAINER))
        case 500 => result.failure(new Exception(ERROR_SERVER))
        case _ => result.failure(new Exception(ERROR_UNKNOWN))
      }
    }
    result.future // return the future
  }

  def stopContainer(containerId: String, waitSecs: Int = 0) : Future[String] = {
    val result = Promise[String]
    Source.single(HttpRequest(POST, uri = SERVER_HTTP + URL_CONTAINER + "/" + containerId + URL_CONTAINER_STOP + "?t=" + waitSecs).withHeaders(
      RawHeader(AUTH_CONFIG_HEADER, authEncoded)))
      .via(buildConnectionFlow).runWith(Sink.head).map { response =>
      response.status.intValue match {
        case 204 => result.success(containerId)
        case 304 => result.failure(new Exception(ERROR_CONTAINER_ALREADY_STOPPED))
        case 404 => result.failure(new Exception(ERROR_NO_SUCH_CONTAINER))
        case 500 => result.failure(new Exception(ERROR_SERVER))
      }
    }
    result.future // return the future
  }

  def killContainer(containerId: String) : Future[String] = {
    val result = Promise[String]
    Source.single(HttpRequest(POST, uri = SERVER_HTTP + URL_CONTAINER + "/" + containerId + URL_CONTAINER_KILL).withHeaders(
      RawHeader(AUTH_CONFIG_HEADER, authEncoded)))
      .via(buildConnectionFlow).runWith(Sink.head).map { response =>
      response.status.intValue match {
        case 204 => result.success(containerId)
        case 404 => result.failure(new Exception(ERROR_NO_SUCH_CONTAINER))
        case 500 => result.failure(new Exception(ERROR_SERVER))
      }
    }
    result.future // return the future
  }

  def restartContainer(containerId: String, waitSecs: Int = 0) : Future[String] = {
    val result = Promise[String]
    Source.single(HttpRequest(POST, uri = SERVER_HTTP + URL_CONTAINER + "/" + containerId + URL_CONTAINER_RESTART +"?t=" + waitSecs).withHeaders(
      RawHeader(AUTH_CONFIG_HEADER, authEncoded)))
      .via(buildConnectionFlow).runWith(Sink.head).map { response =>
      response.status.intValue match {
        case 204 => result.success(containerId)
        case 404 => result.success(ERROR_NO_SUCH_CONTAINER)
        case 500 => result.failure(new Exception(ERROR_SERVER))
      }
    }
    result.future // return the future
  }

  def pauseContainer(containerId: String) : Future[String] = {
    val result = Promise[String]
    Source.single(HttpRequest(POST, uri = SERVER_HTTP + URL_CONTAINER + "/" + containerId + URL_CONTAINER_PAUSE).withHeaders(
      RawHeader(AUTH_CONFIG_HEADER, authEncoded)))
      .via(buildConnectionFlow).runWith(Sink.head).map { response =>
      response.status.intValue match {
        case 204 => result.success(containerId)
        case 404 => result.success(ERROR_NO_SUCH_CONTAINER)
        case 500 => result.failure(new Exception(ERROR_SERVER))
      }
    }
    result.future // return the future
  }

  def unpauseContainer(containerId: String) : Future[String] = {
    val result = Promise[String]
    Source.single(HttpRequest(POST, uri = SERVER_HTTP + URL_CONTAINER + "/" + containerId + URL_CONTAINER_UNPAUSE).withHeaders(
      RawHeader(AUTH_CONFIG_HEADER, authEncoded)))
      .via(buildConnectionFlow).runWith(Sink.head).map { response =>
      response.status.intValue match {
        case 204 => result.success(containerId)
        case 404 => result.success(ERROR_NO_SUCH_CONTAINER)
        case 500 => result.failure(new Exception(ERROR_SERVER))
      }
    }
    result.future // return the future
  }

  def listContainers(args:ContainerParam *) : Future[List[Container]] = {
    val result = Promise[List[Container]]
    val url = if (args.size > 0){
      SERVER_HTTP + URL_CONTAINER_JSON + "?" + args.map((i: ContainerParam) => i.field + "=" + (if(i.value.nonEmpty) {i.value.get} else {""})).mkString("&")
    } else {SERVER_HTTP + URL_CONTAINER_JSON}
    Source.single(HttpRequest(GET, uri = url).withHeaders(
      RawHeader(AUTH_CONFIG_HEADER, authEncoded)))
      .via(buildConnectionFlow).runWith(Sink.head).map { response =>
      response.status.intValue match {
        case 200 => result.completeWith(Unmarshal(response.entity).to[List[Container]])
        case 400 => result.failure(new Exception(ERROR_BAD_PARAMETER))
        case 500 => result.failure(new Exception(ERROR_SERVER))
        case _ => result.failure(new Exception(ERROR_UNKNOWN))
      }
    }
    result.future // return the future
  }

  def inspectContainer(containerId: String) : Future[InspectContainerResponse] = {
    val result = Promise[InspectContainerResponse]
    Source.single(HttpRequest(GET, uri = SERVER_HTTP + URL_CONTAINER + "/" + containerId + URL_JSON).withHeaders(
      RawHeader(AUTH_CONFIG_HEADER, authEncoded)))
      .via(buildConnectionFlow).runWith(Sink.head).map { response =>
      response.status.intValue match {
        case 200 => result.completeWith(Unmarshal(response.entity).to[InspectContainerResponse])
        case 404 => result.failure(new Exception(ERROR_NO_SUCH_CONTAINER))
        case 500 => result.failure(new Exception(ERROR_SERVER))
        case _ => result.failure(new Exception(ERROR_UNKNOWN))
      }
    }
    result.future // return the future
  }
}

