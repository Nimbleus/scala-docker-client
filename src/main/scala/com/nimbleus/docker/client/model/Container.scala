/**
 * Copyright (C) 2013 Nimbleus LLC.
 */
package com.nimbleus.docker.client.model

/**
 * This model represents a docker container's port mappings.
 * User: cstewart
 */
case class Port(PrivatePort: Int, PublicPort: Int, Type: String)

/**
 * This model represents a docker container.
 * User: cstewart
 */
case class Container(Id: String, Image :String, Command: String, Created: Int, Status: String, Ports : Option[List[Port]], SizeRw: Int, SizeRootFs: Int, Names: List[String])

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

trait ContainerParam {
  def field: String
  def value: Option[Any]
}

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

case class CreateConfig(Image: String, PortSpecs: Option[String], Env: Option[String], Cmd: List[String])

case class CreateContainerResponse(Id: String)


/*
{
  "Id": "4fa6e0f0c6786287e131c3852c58a2e01cc697a68231826813597e4994f1d6e2",
  "Created": "2013-05-07T14:51:42.041847+02:00",
  "Path": "date",
  "Args": [],
  "Config": {
    "Hostname": "4fa6e0f0c678",
    "User": "",
    "Memory": 0,
    "MemorySwap": 0,
    "AttachStdin": false,
    "AttachStdout": true,
    "AttachStderr": true,
    "PortSpecs": null,
    "Tty": false,
    "OpenStdin": false,
    "StdinOnce": false,
    "Env": null,
    "Cmd": [
    "date"
    ],
    "Dns": null,
    "Image": "base",
    "Volumes": {},
    "VolumesFrom": "",
    "WorkingDir":""

  },
  "State": {
    "Running": false,
    "Pid": 0,
    "ExitCode": 0,
    "StartedAt": "2013-05-07T14:51:42.087658+02:01360",
    "Ghost": false
  },
  "Image": "b750fe79269d2ec9a3c593ef05b4332b1d1a02a62b4accb2c21d589ff2f5f2dc",
  "NetworkSettings": {
    "IpAddress": "",
    "IpPrefixLen": 0,
    "Gateway": "",
    "Bridge": "",
    "PortMapping": null
  },
  "SysInitPath": "/home/kitty/go/src/github.com/dotcloud/docker/bin/docker",
  "ResolvConfPath": "/etc/resolv.conf",
  "Volumes": {},
  "HostConfig": {
    "Binds": null,
    "ContainerIDFile": "",
    "LxcConf": [],
    "Privileged": false,
    "PortBindings": {
    "80/tcp": [
  {
    "HostIp": "0.0.0.0",
    "HostPort": "49153"
  }
    ]
  },
    "Links": null,
    "PublishAllPorts": false
  }
}
*/

//actual results
/*
{
  "ID": "ebbbc8facfded89530e670c882577be5f93fe278af58fdc07b9bac8785c479a2",
  "Created": "2013-12-12T01:36:48.845903837Z",
  "Path": "/bin/sh",
  "Args": [
  "-c",
  "while true; do echo hello world; sleep 1; done"
  ],
  "Config": {
    "Hostname": "ebbbc8facfde",
    "Domainname": "",
    "User": "",
    "Memory": 0,
    "MemorySwap": 0,
    "CpuShares": 0,
    "AttachStdin": false,
    "AttachStdout": false,
    "AttachStderr": false,
    "PortSpecs": null,
    "ExposedPorts": {},
    "Tty": false,
    "OpenStdin": false,
    "StdinOnce": false,
    "Env": null,
    "Cmd": [
    "/bin/sh",
    "-c",
    "while true; do echo hello world; sleep 1; done"
    ],
    "Dns": null,
    "Image": "ubuntu",
    "Volumes": {},
    "VolumesFrom": "",
    "WorkingDir": "",
    "Entrypoint": null,
    "NetworkDisabled": false
  },
  "State": {
    "Running": true,
    "Pid": 1596,
    "ExitCode": 0,
    "StartedAt": "2013-12-12T01:36:48.856143898Z",
    "FinishedAt": "0001-01-01T00:00:00Z",
    "Ghost": false
  },
  "Image": "8dbd9e392a964056420e5d58ca5cc376ef18e2de93b5cc90e868a1bbc8318c1c",
  "NetworkSettings": {
    "IPAddress": "172.17.0.2",
    "IPPrefixLen": 16,
    "Gateway": "172.17.42.1",
    "Bridge": "docker0",
    "PortMapping": null,
    "Ports": {}
  },
  "SysInitPath": "/usr/bin/docker",
  "ResolvConfPath": "/etc/resolv.conf",
  "HostnamePath": "/var/lib/docker/containers/ebbbc8facfded89530e670c882577be5f93fe278af58fdc07b9bac8785c479a2/hostname",
  "HostsPath": "/var/lib/docker/containers/ebbbc8facfded89530e670c882577be5f93fe278af58fdc07b9bac8785c479a2/hosts",
  "Name": "/trusting_lumiere",
  "Driver": "aufs",
  "Volumes": {},
  "VolumesRW": {}
}
*/








