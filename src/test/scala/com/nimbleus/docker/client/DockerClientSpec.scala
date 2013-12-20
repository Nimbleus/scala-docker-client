/**
 * Copyright (C) 2013 Nimbleus LLC.
 */
package com.nimbleus.docker.client

import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._
import akka.util.Timeout
import akka.actor._

/**
 * This is the unit test for the docker client
 * User: cstewart
 */
class DockerClientSpec {
  implicit val timeout: Timeout = 5.seconds
  val testConf = ConfigFactory.parseString("""
    akka.event-handlers = ["akka.testkit.TestEventListener"]
    akka.loglevel = WARNING
    akka.io.tcp.trace-logging = off
    spray.can.host-connector.max-retries = 4
    spray.can.client.request-timeout = 400ms
    spray.can.client.user-agent-header = "RequestMachine"""")
  implicit val system = ActorSystem("DockerClientSpec", testConf)
  import system.dispatcher


}
