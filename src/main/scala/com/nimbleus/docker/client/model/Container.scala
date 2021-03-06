/**
 * Copyright (C) 2013 Nimbleus LLC.
 */
package com.nimbleus.docker.client.model

trait ContainerParam {
  def field: String
  def value: Option[Any]
}

object WeaveContainer {
  //8c3ad03c30d3 5a:21:3d:fb:b2:a2 10.2.0.1/16
  def parse(data: Array[String]): List[WeaveContainer] = {
    data.map { line =>
      val tokens = line.split(" ")
      val containerId = tokens(0)
      val ip4 = if (tokens.length >= 3) { Some(tokens(2).substring(0, tokens(2).lastIndexOf("/"))) } else { None }
      val ip6 = if (tokens.length >= 2) { Some(tokens(1)) } else { None }
      WeaveContainer(containerId, ip4, ip6)
    }.toList
  }
}
case class WeaveContainer(containerId: String, ipV4: Option[String], ipV6: Option[String])

// 1/True/true or 0/False/false, Show all containers. Only running containers are shown by default
case class ContainerParamAll(paramValue: Boolean) extends ContainerParam {
  val field = "all"
  val value = Some(paramValue)
}

// Show limit last created containers, include non-running ones
case class ContainerParamLimit() extends ContainerParam {
  val field = "limit"
  val value = None
}

// Show only containers created since Id, include non-running ones
case class ContainerParamSince(paramValue: String) extends ContainerParam {
  val field = "since"
  val value = Some(paramValue)
}

// Show only containers created before Id, include non-running ones
case class ContainerParamBefore(paramValue: String) extends ContainerParam {
  val field = "before"
  val value = Some(paramValue)
}

// 1/True/true or 0/False/false, Show the containers sizes
case class ContainerParamSize(paramValue: Boolean) extends ContainerParam {
  val field = "size"
  val value = Some(paramValue)
}

case class Port(PrivatePort: Int, PublicPort: Option[Int], Type: String)
case class Container(Id: String, Image :String, Command: String, Created: Int, Status: String,
                     Ports : Option[List[Port]], Names: Option[List[String]],
                     Labels: Option[Map[String,String]])

// "Ports": [{"PrivatePort": 2222, "PublicPort": 3333, "Type": "tcp"}]

object Container {
  def getErrorReason(responseCode: Int, errorDescription: String) : String = {
    responseCode match {
      case 400 => "Bad container parameter: " + errorDescription
      case 500 => "Bad container request: "  + errorDescription
      case _ => "Unknown container error: " + errorDescription
    }
  }
}

case class ContainerProcess(Titles: List[String], Processes: List[List[String]])
object ContainerProcessHelper {
  def getErrorReason(responseCode: Int, errorDescription: String) : String = {
    responseCode match {
      case 404 => "No such container for process request: " + errorDescription
      case 500 => "Bad container process request: "  + errorDescription
      case _ => "Unknown container error: " + errorDescription
    }
  }
}

// create container support
case class HostPort(HostPort: String)
case class StartConfig(PortBindings: Option[Map[String, List[HostPort]]] = None)
case class DockerPortBinding(privatePort: Int, publicPort: Option[Int] = None,
                            protocol: Option[String] = None, hostIp: Option[String] = None)
case class CreateConfig(Image: String, Labels: Option[Map[String,String]], Env: List[String], Cmd: List[String],
                        ExposedPorts: Option[Map[String, Option[DockerPortBinding]]] = None,
                        HostConfig: Option[CreateHostConfig])
/*
case class CreateConfig(Image: String, Labels: Option[Map[String,String]], Env: List[String], Cmd: List[String], Entrypoint: List[String],
                        ExposedPorts: Option[Map[String, Option[DockerPortBinding]]] = None,
                        HostConfig: Option[CreateHostConfig])
*/
case class CreateHostConfig(Memory: Option[Int], MemorySwap: Option[Int], CpuShares: Option[Int],
                            Privileged: Boolean, PortBindings: Option[Map[String, List[HostPort]]] = None)
case class CreateContainerResponse(Id: String)

// inspect container
case class Port2(HostIp: String, HostPort: String)

case class ContainerHostConfig(Binds: Option[List[String]], ContainerIDFile: String, LxcConf: Option[List[ContainerLxcConf]],
                               Privileged: Boolean, PortBindings: Option[Map[String, Option[List[Port2]]]], Links: Option[List[String]],
                               PublishAllPorts: Boolean, Memory: Int, MemorySwap: Int, CpuShares: Int, CpuPeriod: Int,
                               CpusetCpus: String, CpusetMems: String, CpuQuota: Int, BlkioWeight: Int)
case class ContainerNetworkSettings(IPAddress: String, IPPrefixLen: Int, Gateway: String, Bridge: String,
                                    PortMapping: Option[Map[String,Map[String, String]]], Ports: Option[Map[String, Option[List[Port2]]]])
case class ContainerState(Running: Boolean, Pid: Int, ExitCode: Int, StartedAt: String, FinishedAt: String, Paused: Boolean, Restarting: Boolean,
                          OOMKilled: Boolean, Dead: Boolean, Error: String, Status: String)
case class ContainerLxcConf(Key: String, Value: String)
case class ContainerConfig(Hostname: String, User: String, AttachStdin: Boolean,
                           AttachStdout: Boolean, AttachStderr: Boolean, Tty: Boolean,
                           OpenStdin: Boolean, StdinOnce: Boolean, Env: Option[List[String]], Cmd: Option[List[String]],
                           Labels: Option[Map[String,String]])
case class InspectContainerResponse(Id: String, Created: String, Path: String, Args: Option[List[String]],
                                    Config: ContainerConfig, State: ContainerState, Image: String,
                                    NetworkSettings: ContainerNetworkSettings, SysInitPath: Option[String],
                                    ResolvConfPath: String, HostnamePath: String, HostsPath: String,
                                    Name: String, Volumes: Option[Map[String, String]], HostConfig: ContainerHostConfig)
object InspectContainerResponse {
  def getErrorReason(responseCode: Int, errorDescription: String) : String = {
    responseCode match {
      case 404 => "No such container for inspection request: " + errorDescription
      case 500 => "Bad container process request: "  + errorDescription
      case _ => "Unknown container inspection error: " + errorDescription
    }
  }
}





