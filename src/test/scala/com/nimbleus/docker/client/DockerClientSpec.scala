/**
 * Copyright (C) 2013 Nimbleus LLC.
 */
package com.nimbleus.docker.client

import org.scalatest.{Matchers, FunSuite}
import scala.concurrent._
import ExecutionContext.Implicits.global
import org.scalatest.concurrent.ScalaFutures
import org.scalatest._
import com.nimbleus.docker.client.model.Version
import scala.util.{Failure, Success}
import org.scalatest.time.{ Millis, Seconds, Span }

/**
 * This is the unit test for the docker client
 * User: cstewart
 */
class DockerClientSpec extends FunSuite with Matchers with ScalaFutures {
  implicit override val patienceConfig =
    PatienceConfig(timeout = Span(5, Seconds), interval = Span(5, Millis))
  val serverUrl : String = "http://localhost:4243"
  // TODO setup docker container
  test("Get docker versions") {
    val futureResult: Future[Version] = DockerClient.getVersion(serverUrl)
    whenReady(futureResult) { result =>
      result.Version.length should be > 1
      result.GoVersion.length should be > 1
      result.GitCommit.length should be > 1
    }
  }
}