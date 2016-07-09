/**
 * Copyright (C) 2013 Nimbleus LLC.
 */
package com.nimbleus.docker.client

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.testkit.ImplicitSender
import akka.testkit.TestKit
import org.scalatest._
import scala.concurrent._
import org.scalatest.concurrent.ScalaFutures
import com.nimbleus.docker.client.model._
import org.scalatest.time.{Millis, Seconds, Span}
import akka.stream.ActorMaterializer
import scala.concurrent.ExecutionContext.Implicits.global

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
with WordSpecLike with Matchers with ScalaFutures with BeforeAndAfterAll {
  implicit override val patienceConfig =
    PatienceConfig(timeout = Span(300, Seconds), interval = Span(5, Millis))

  def this() = this(ActorSystem("DockerClientSpec"))

  override def afterAll {
    Http().shutdownAllConnectionPools() andThen { case _ => TestKit.shutdownActorSystem(system) }
  }

  private val host = system.settings.config.getString("docker-remote-api-host")
  private val port = system.settings.config.getInt("docker-remote-api-port")
  private val serverUrl: String = s"http://$host:$port"
  private val authUser = "nimbleusadmin"
  private val authPassword = "X6T&5jN5Jb!kuU08"
  private val authEmail = "cstewart@nimbleus.com"
  private val authAuth = ""

  //159.203.72.31:12375/images/create?fromImage=nimbleusadmin/akka-cluster-seed-node

  //implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  "The scala docker client library" should {
    "get the current version" in {
      val auth = AuthConfig(authUser, authPassword , authAuth, authEmail)
      val dockerClient = new DockerClient(host, Some(port), auth)
      val futureResult: Future[Version] = dockerClient.getVersion
      whenReady(futureResult) { result =>
        result.Os should be ("linux")
      }
    }

    "get docker info" in {
      val auth = AuthConfig(authUser, authPassword , authAuth, authEmail)
      val dockerClient = new DockerClient(host, Some(port), auth)
      val futureResult: Future[Info] = dockerClient.getInfo
      whenReady(futureResult) { result =>
        println(result)
      }
    }

    // TODO create a test container image
    "download docker image" in {
      val auth = AuthConfig(authUser, authPassword , authAuth, authEmail)
      val dockerClient = new DockerClient(host, Some(port), auth)
      val futureResult: Future[Boolean] = dockerClient.downloadImage("nimbleusadmin/akka-cluster-seed-node", None)
      whenReady(futureResult) { result =>
        result should be (true)
      }
    }

    "determine that image exists" in {
      val auth = AuthConfig(authUser, authPassword , authAuth, authEmail)
      val dockerClient = new DockerClient(host, Some(port), auth)
      val futureResult: Future[Boolean] = dockerClient.imageExist("nimbleusadmin/akka-cluster-seed-node", None)
      whenReady(futureResult) { result =>
        result should be (true)
      }
    }

    "get the docker images" in {
      val auth = AuthConfig(authUser, authPassword , authAuth, authEmail)
      val dockerClient = new DockerClient(host, Some(port), auth)
      val futureResult: Future[List[Image]] = dockerClient.getImages
      whenReady(futureResult) { result =>
        println(result)
      }
    }

    "create container" in {
      val auth = AuthConfig(authUser, authPassword , authAuth, authEmail)
      val dockerClient = new DockerClient(host, Some(port), auth)
      val env: List[String] = List("JAVA_OPTS=-Dport=8500")
      val cmd: List[String] = List()
      val hostConfig = CreateHostConfig(Some(128000000), None, None, false, Some(Map("8500/tcp" -> List(HostPort("9000")))))
      val config = CreateConfig("nimbleusadmin/akka-cluster-seed-node:2.3.10", None, env, cmd, Some(Map("9000/tcp" -> None)), Some(hostConfig))
      val futureResult2: Future[Boolean] = dockerClient.imageExist("nimbleusadmin/akka-cluster-seed-node", Some("2.3.10"))
      whenReady(futureResult2) { result =>
        result should be (true)
        val futureResult3: Future[CreateContainerResponse] = dockerClient.createContainer(config, None)
        whenReady(futureResult3) { result3 =>
          result3.Id.length should be > 0
          val futureResult4: Future[String] = dockerClient.removeContainer(result3.Id, true)
          whenReady(futureResult4) { result4 =>
            result4 should equal (result3.Id)
          }
        }
      }
    }

    "start container" in {
      val auth = AuthConfig(authUser, authPassword , authAuth, authEmail)
      val dockerClient = new DockerClient(host, Some(port), auth)
      val env: List[String] = List("JAVA_OPTS=-Dport=8500")
      val cmd: List[String] = List()
      val hostConfig = CreateHostConfig(Some(128000000), None, None, false, Some(Map("8500/tcp" -> List(HostPort("9000")))))
      val config = CreateConfig("nimbleusadmin/akka-cluster-seed-node:2.3.10", None, env, cmd, Some(Map("9000/tcp" -> None)), Some(hostConfig))
      val futureResult2: Future[Boolean] = dockerClient.imageExist("nimbleusadmin/akka-cluster-seed-node", Some("2.3.10"))
      whenReady(futureResult2) { result =>
        result should be (true)
        val futureResult3: Future[CreateContainerResponse] = dockerClient.createContainer(config, None)
        whenReady(futureResult3) { result3 =>
          result3.Id.length should be > 0
          val futureResult4: Future[String] = dockerClient.startContainer(result3.Id)
          whenReady(futureResult4) { result4 =>
            result4 should equal (result3.Id)
            val futureResult5: Future[String] = dockerClient.removeContainer(result4, true)
            whenReady(futureResult5) { result5 =>
              result4 should equal (result4)
            }
          }
        }
      }
    }

    "stop container" in {
      val auth = AuthConfig(authUser, authPassword , authAuth, authEmail)
      val dockerClient = new DockerClient(host, Some(port), auth)
      val env: List[String] = List("JAVA_OPTS=-Dport=8500")
      val cmd: List[String] = List()
      val hostConfig = CreateHostConfig(Some(128000000), None, None, false, Some(Map("8500/tcp" -> List(HostPort("9000")))))
      val config = CreateConfig("nimbleusadmin/akka-cluster-seed-node:2.3.10", None, env, cmd, Some(Map("9000/tcp" -> None)), Some(hostConfig))
      val futureResult: Future[Boolean] = dockerClient.imageExist("nimbleusadmin/akka-cluster-seed-node", Some("2.3.10"))
      whenReady(futureResult) { result =>
        result should be (true)
        val futureResult2: Future[CreateContainerResponse] = dockerClient.createContainer(config, None)
        whenReady(futureResult2) { result2 =>
          result2.Id.length should be > 0
          val futureResult3: Future[String] = dockerClient.startContainer(result2.Id)
          whenReady(futureResult3) { result3 =>
            result3 should equal (result2.Id)
            val futureResult4: Future[String] = dockerClient.stopContainer(result2.Id)
            whenReady(futureResult4) { result4 =>
              result4 should equal (result2.Id)
              val futureResult5: Future[String] = dockerClient.removeContainer(result4, true)
              whenReady(futureResult5) { result5 =>
                result4 should equal (result4)
              }
            }
          }
        }
      }
    }

    "kill container" in {
      val auth = AuthConfig(authUser, authPassword , authAuth, authEmail)
      val dockerClient = new DockerClient(host, Some(port), auth)
      val env: List[String] = List("JAVA_OPTS=-Dport=8500")
      val cmd: List[String] = List()
      val hostConfig = CreateHostConfig(Some(128000000), None, None, false, Some(Map("8500/tcp" -> List(HostPort("9000")))))
      val config = CreateConfig("nimbleusadmin/akka-cluster-seed-node:2.3.10", None, env, cmd, Some(Map("9000/tcp" -> None)), Some(hostConfig))
      val futureResult: Future[Boolean] = dockerClient.imageExist("nimbleusadmin/akka-cluster-seed-node", Some("2.3.10"))
      whenReady(futureResult) { result =>
        result should be (true)
        val futureResult2: Future[CreateContainerResponse] = dockerClient.createContainer(config, None)
        whenReady(futureResult2) { result2 =>
          result2.Id.length should be > 0
          val futureResult3: Future[String] = dockerClient.startContainer(result2.Id)
          whenReady(futureResult3) { result3 =>
            result3 should equal (result2.Id)
            val futureResult4: Future[String] = dockerClient.killContainer(result2.Id)
            whenReady(futureResult4) { result4 =>
              result4 should equal (result2.Id)
              val futureResult5: Future[String] = dockerClient.removeContainer(result4, true)
              whenReady(futureResult5) { result5 =>
                result4 should equal (result4)
              }
            }
          }
        }
      }
    }

    "restart container" in {
      val auth = AuthConfig(authUser, authPassword , authAuth, authEmail)
      val dockerClient = new DockerClient(host, Some(port), auth)
      val env: List[String] = List("JAVA_OPTS=-Dport=8500")
      val cmd: List[String] = List()
      val hostConfig = CreateHostConfig(Some(128000000), None, None, false, Some(Map("8500/tcp" -> List(HostPort("9000")))))
      val config = CreateConfig("nimbleusadmin/akka-cluster-seed-node:2.3.10", None, env, cmd, Some(Map("9000/tcp" -> None)), Some(hostConfig))
      val futureResult: Future[Boolean] = dockerClient.imageExist("nimbleusadmin/akka-cluster-seed-node", Some("2.3.10"))
      whenReady(futureResult) { result =>
        result should be (true)
        val futureResult2: Future[CreateContainerResponse] = dockerClient.createContainer(config, None)
        whenReady(futureResult2) { result2 =>
          result2.Id.length should be > 0
          val futureResult3: Future[String] = dockerClient.startContainer(result2.Id)
          whenReady(futureResult3) { result3 =>
            result3 should equal (result2.Id)
            val futureResult4: Future[String] = dockerClient.restartContainer(result2.Id)
            whenReady(futureResult4) { result4 =>
              result4 should equal (result2.Id)
              val futureResult5: Future[String] = dockerClient.removeContainer(result4, true)
              whenReady(futureResult5) { result5 =>
                result4 should equal (result4)
              }
            }
          }
        }
      }
    }

    "pause and unpause container" in {
      val auth = AuthConfig(authUser, authPassword , authAuth, authEmail)
      val dockerClient = new DockerClient(host, Some(port), auth)
      val env: List[String] = List("JAVA_OPTS=-Dport=8500")
      val cmd: List[String] = List()
      val hostConfig = CreateHostConfig(Some(128000000), None, None, false, Some(Map("8500/tcp" -> List(HostPort("9000")))))
      val config = CreateConfig("nimbleusadmin/akka-cluster-seed-node:2.3.10", None, env, cmd, Some(Map("9000/tcp" -> None)), Some(hostConfig))
      val futureResult: Future[Boolean] = dockerClient.imageExist("nimbleusadmin/akka-cluster-seed-node", Some("2.3.10"))
      whenReady(futureResult) { result =>
        result should be (true)
        val futureResult2: Future[CreateContainerResponse] = dockerClient.createContainer(config, None)
        whenReady(futureResult2) { result2 =>
          result2.Id.length should be > 0
          val futureResult3: Future[String] = dockerClient.startContainer(result2.Id)
          whenReady(futureResult3) { result3 =>
            result3 should equal (result2.Id)
            val futureResult4: Future[String] = dockerClient.pauseContainer(result2.Id)
            whenReady(futureResult4) { result4 =>
              result4 should equal (result2.Id)
              val futureResult5: Future[String] = dockerClient.unpauseContainer(result2.Id)
              whenReady(futureResult5) { result5 =>
                result5 should equal(result2.Id)
                val futureResult6: Future[String] = dockerClient.removeContainer(result4, true)
                whenReady(futureResult6) { result6 =>
                  result6 should equal (result4)
                }
              }
            }
          }
        }
      }
    }

    "list containers" in {
      val auth = AuthConfig(authUser, authPassword , authAuth, authEmail)
      val dockerClient = new DockerClient(host, Some(port), auth)
      val env: List[String] = List("JAVA_OPTS=-Dport=8500")
      val cmd: List[String] = List()
      val hostConfig = CreateHostConfig(Some(128000000), None, None, false, Some(Map("8500/tcp" -> List(HostPort("9000")))))
      val config = CreateConfig("nimbleusadmin/akka-cluster-seed-node:2.3.10", None, env, cmd, Some(Map("9000/tcp" -> None)), Some(hostConfig))
      val futureResult: Future[Boolean] = dockerClient.imageExist("nimbleusadmin/akka-cluster-seed-node", Some("2.3.10"))
      whenReady(futureResult) { result =>
        result should be (true)
        val futureResult2: Future[CreateContainerResponse] = dockerClient.createContainer(config, None)
        whenReady(futureResult2) { result2 =>
          result2.Id.length should be > 0
          val p1: ContainerParam = ContainerParamAll(true)
          val futureResult3: Future[List[Container]] = dockerClient.listContainers(p1)
          whenReady(futureResult3) { result3 =>
            val futureResult4: Future[String] = dockerClient.removeContainer(result2.Id, true)
            whenReady(futureResult4) { result4 =>
              result4 should equal(result2.Id)
            }
          }
        }
      }
    }

    "inspect container" in {
      val auth = AuthConfig(authUser, authPassword , authAuth, authEmail)
      val dockerClient = new DockerClient(host, Some(port), auth)
      val env: List[String] = List("JAVA_OPTS=-Dport=8500")
      val cmd: List[String] = List()
      val hostConfig = CreateHostConfig(Some(128000000), None, None, false, Some(Map("8500/tcp" -> List(HostPort("9000")))))
      val config = CreateConfig("nimbleusadmin/akka-cluster-seed-node:2.3.10", None, env, cmd, Some(Map("9000/tcp" -> None)), Some(hostConfig))
      val futureResult: Future[Boolean] = dockerClient.imageExist("nimbleusadmin/akka-cluster-seed-node", Some("2.3.10"))
      whenReady(futureResult) { result =>
        result should be (true)
        val futureResult2: Future[CreateContainerResponse] = dockerClient.createContainer(config, None)
        whenReady(futureResult2) { result2 =>
          result2.Id.length should be > 0
          val futureResult3: Future[InspectContainerResponse] = dockerClient.inspectContainer(result2.Id)
          whenReady(futureResult3) { result3 =>
            val futureResult4: Future[String] = dockerClient.removeContainer(result2.Id, true)
            whenReady(futureResult4) { result4 =>
              result4 should equal(result2.Id)
            }
          }
        }
      }
    }
  }
}