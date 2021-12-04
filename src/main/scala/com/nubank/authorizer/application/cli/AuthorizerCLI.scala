package com.nubank.authorizer.application.cli

import com.nubank.authorizer.application.cli.model.json._
import com.nubank.authorizer.application.cli.model.requests.{CreateAccountRequest, ProcessTransactionRequest, Request}
import com.nubank.authorizer.domain.Authorizer
import com.nubank.authorizer.infrastructure.InMemoryAccountRepository
import io.circe.parser.decode
import cats.implicits._
import com.nubank.authorizer.application.cli.model.ConsoleResponse
import io.circe.syntax._

object AuthorizerCLI extends App {
  type ParsingError[A] = Either[io.circe.Error, A]

  val authorizer = new Authorizer(new InMemoryAccountRepository())

  val authorizations = scala.io.Source.stdin.getLines().toList.map(decode[Request]).nested.map {
    case car: CreateAccountRequest => authorizer.createAccount(car.asModel)
    case ptr: ProcessTransactionRequest => authorizer.authorize(ptr.asModel)
  }

  authorizations.map(ConsoleResponse.from).value.foreach {
    case Left(err) => println(s"Decoder Error. Message: ${err.getMessage}")
    case Right(response) => println(response.asJson.noSpaces)
  }
}
