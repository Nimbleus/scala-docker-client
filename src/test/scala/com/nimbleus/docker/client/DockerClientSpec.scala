/**
 * Copyright (C) 2013 Nimbleus LLC.
 */
package com.nimbleus.docker.client

import akka.actor.ActorSystem
import akka.testkit.ImplicitSender
import akka.testkit.TestKit
import org.scalatest._
import scala.concurrent._
import org.scalatest.concurrent.ScalaFutures
import com.nimbleus.docker.client.model._
import org.scalatest.time.{ Millis, Seconds, Span }

/**
 * This is the unit test for the docker client
 * User: cstewart
 *
 * (Mac OS) =>  We strongly recommend against running Boot2Docker with an unencrypted
 * Docker socket for security reasons, but if you have tools that cannot
 * be easily switched, you can disable it by adding DOCKER_TLS=no to
 * your /var/lib/boot2docker/profile file on the persistent partition inside
 * the Boot2Docker virtual
 * machine (use boot2docker ssh sudo vi /var/lib/boot2docker/profile).
 *
 */
class DockerClientSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
                with WordSpecLike with Matchers with ScalaFutures with BeforeAndAfterAll{
  implicit override val patienceConfig =
    PatienceConfig(timeout = Span(5, Seconds), interval = Span(5, Millis))

  def this() = this(ActorSystem("DockerClientSpec"))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  val host = system.settings.config.getString("docker-remote-api-host")
  val port = system.settings.config.getInt("docker-remote-api-port")

  val serverUrl : String = s"http://$host:$port"

  "The scala docker client library" should {
    "get the current version" in {
      val futureResult: Future[Version] = DockerClient.getVersion(serverUrl)
      whenReady(futureResult) { result =>
        result.Version.length should be > 1
        result.GoVersion.length should be > 1
        result.GitCommit.length should be > 1
      }
    }
    "get the docker info" in {
      val futureResult: Future[Info] = DockerClient.getInfo(serverUrl)
      whenReady(futureResult) { result =>
        result.OperatingSystem.length should be > 1
      }
    }
    "get the docker images" in {
      val futureResult: Future[List[Image]] = DockerClient.getImages(serverUrl)
      whenReady(futureResult) { result =>
        result.length should be >= 0
      }
    }
    "get the list of active containers" in {
      val p1: ContainerParam = ContainerParamAll(false)
      val futureResult: Future[List[Container]] = DockerClient.listContainers(serverUrl, p1)
      whenReady(futureResult) { result =>
        result.length should be >= 0
      }
    }

    "create a container" in {
      val env: List[String] = List()
      val cmd: List[String] = List()
      val ports: List[String] = List()
      val exposedPort = DockerPortBinding(80)
      // Runs the basic hello world container and exists
      val config = CreateConfig("91c95931e552", ports, env, cmd, Some(Map("80/tcp" -> None)))
      val futureResult: Future[CreateContainerResponse] = DockerClient.createContainer(serverUrl, config, None)
      whenReady(futureResult) { result =>
        result.Id.length should be > 0
        println("started container with id => " + result.Id)
        val removeResponse = DockerClient.removeContainer(serverUrl, result.Id, true)
        whenReady(removeResponse) { removeRes =>
          removeRes.length should be > 0
          println(removeRes)
        }
      }
    }

    "start container" in {
      val env: List[String] = List()
      val cmd: List[String] = List("/bin/sh", "-c", "while true; do echo hello world; sleep 1; done;")
      val ports: List[String] = List()
      val exposedPort = DockerPortBinding(80)
      // Runs the basic hello world container and exists
      val config = CreateConfig("b7cf8f0d9e82", ports, env, cmd, Some(Map("80/tcp" -> None)))
      val futureResult: Future[CreateContainerResponse] = DockerClient.createContainer(serverUrl, config, None)
      whenReady(futureResult) { result =>
        result.Id.length should be > 0
        val startResponse = DockerClient.startContainer(serverUrl, result.Id)
        whenReady(startResponse) { startRes =>
          startRes.length should be > 0
          println(startRes)
          val removeResponse = DockerClient.removeContainer(serverUrl, result.Id, true)
          whenReady(removeResponse) { removeRes =>
            removeRes.length should be > 0
            println(removeRes)
          }
        }
      }
    }

    "stop container" in {
      val env: List[String] = List()
      val cmd: List[String] = List("/bin/sh", "-c", "while true; do echo hello world; sleep 1; done;")
      val ports: List[String] = List()
      val exposedPort = DockerPortBinding(80)
      // Runs the basic hello world container and exists
      val config = CreateConfig("b7cf8f0d9e82", ports, env, cmd, Some(Map("80/tcp" -> None)))
      val futureResult: Future[CreateContainerResponse] = DockerClient.createContainer(serverUrl, config, None)
      whenReady(futureResult) { result =>
        result.Id.length should be > 0
        val startResponse = DockerClient.startContainer(serverUrl, result.Id)
        whenReady(startResponse) { startRes =>
          startRes.length should be > 0
          println(startRes)
          val stopResponse = DockerClient.stopContainer(serverUrl, result.Id)
          whenReady(stopResponse) { stopRes =>
            stopRes.length should be > 0
            println(stopRes)
            val removeResponse = DockerClient.removeContainer(serverUrl, result.Id, true)
            whenReady(removeResponse) { removeRes =>
              removeRes.length should be > 0
              println(removeRes)
            }
          }
        }
      }
    }

    "restart container" in {
      val env: List[String] = List()
      val cmd: List[String] = List("/bin/sh", "-c", "while true; do echo hello world; sleep 1; done;")
      val ports: List[String] = List()
      val exposedPort = DockerPortBinding(80)
      // Runs the basic hello world container and exists
      val config = CreateConfig("b7cf8f0d9e82", ports, env, cmd, Some(Map("80/tcp" -> None)))
      val futureResult: Future[CreateContainerResponse] = DockerClient.createContainer(serverUrl, config, None)
      whenReady(futureResult) { result =>
        result.Id.length should be > 0
        val startResponse = DockerClient.startContainer(serverUrl, result.Id)
        whenReady(startResponse) { startRes =>
          startRes.length should be > 0
          println(startRes)
          val restartResponse = DockerClient.restartContainer(serverUrl, result.Id)
          whenReady(restartResponse) { restartRes =>
            restartRes.length should be > 0
            println(restartRes)
            val removeResponse = DockerClient.removeContainer(serverUrl, result.Id, true)
            whenReady(removeResponse) { removeRes =>
              removeRes.length should be > 0
              println(removeRes)
            }
          }
        }
      }
    }

    "kill container" in {
      val env: List[String] = List()
      val cmd: List[String] = List("/bin/sh", "-c", "while true; do echo hello world; sleep 1; done;")
      val ports: List[String] = List()
      val exposedPort = DockerPortBinding(80)
      // Runs the basic hello world container and exists
      val config = CreateConfig("b7cf8f0d9e82", ports, env, cmd, Some(Map("80/tcp" -> None)))
      val futureResult: Future[CreateContainerResponse] = DockerClient.createContainer(serverUrl, config, None)
      whenReady(futureResult) { result =>
        result.Id.length should be > 0
        val killResponse = DockerClient.killContainer(serverUrl, result.Id)
        whenReady(killResponse) { killRes =>
          killRes.length should be > 0
          println(killRes)
          // TODO inspect container should be non null
          val removeResponse = DockerClient.removeContainer(serverUrl, result.Id, true)
          whenReady(removeResponse) { removeRes =>
            removeRes.length should be > 0
            println(removeRes)
          }
        }
      }
    }

    "remove container" in {
      val env: List[String] = List()
      val cmd: List[String] = List("/bin/sh", "-c", "while true; do echo hello world; sleep 1; done;")
      val ports: List[String] = List()
      val exposedPort = DockerPortBinding(80)
      // Runs the basic hello world container and exists
      val config = CreateConfig("b7cf8f0d9e82", ports, env, cmd, Some(Map("80/tcp" -> None)))
      val futureResult: Future[CreateContainerResponse] = DockerClient.createContainer(serverUrl, config, None)
      whenReady(futureResult) { result =>
        result.Id.length should be > 0
        val removeResponse = DockerClient.removeContainer(serverUrl, result.Id, true)
        whenReady(removeResponse) { removeRes =>
          removeRes.length should be > 0
          println(removeRes)
          // TODO inspect container should be null
        }
      }
    }

    "inspect container" in {
      val env: List[String] = List()
      val cmd: List[String] = List("/bin/sh", "-c", "while true; do echo hello world; sleep 1; done;")
      val ports: List[String] = List()
      val exposedPort = DockerPortBinding(80)
      // Runs the basic hello world container and exists
      val config = CreateConfig("b7cf8f0d9e82", ports, env, cmd, Some(Map("80/tcp" -> None)))
      val futureResult: Future[CreateContainerResponse] = DockerClient.createContainer(serverUrl, config, None)
      whenReady(futureResult) { result =>
        result.Id.length should be > 0
        val startResponse = DockerClient.startContainer(serverUrl, result.Id)
        whenReady(startResponse) { startRes =>
          startRes.length should be > 0
          println(startRes)
          val inspectResponse = DockerClient.inspectContainer(serverUrl, result.Id)
          whenReady(inspectResponse) { inspectRes =>
            inspectRes.Id.length should be > 0
            println(inspectRes)
            val removeResponse = DockerClient.removeContainer(serverUrl, result.Id, true)
            whenReady(removeResponse) { removeRes =>
              removeRes.length should be > 0
              println(removeRes)
            }
          }
        }
      }
    }
  }
}