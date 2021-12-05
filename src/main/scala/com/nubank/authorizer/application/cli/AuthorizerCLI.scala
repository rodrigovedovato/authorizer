package com.nubank.authorizer.application.cli

import com.nubank.authorizer.application.cli.model.json._
import com.nubank.authorizer.application.cli.model.ConsoleResponse
import io.circe.syntax._

object AuthorizerCLI extends App {
  def success(response: ConsoleResponse): Unit = {
    println(response.asJson.noSpaces)
  }

  def fail(err: io.circe.Error): Unit = {
    println(s"Decoding Error ${err.getMessage}")
  }

  new Program().run(scala.io.Source.stdin.getLines().toList, success, fail)
}
