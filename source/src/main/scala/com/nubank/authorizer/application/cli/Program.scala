package com.nubank.authorizer.application.cli

import com.nubank.authorizer.application.cli.model.json._
import com.nubank.authorizer.application.cli.model.requests.{CreateAccountRequest, ProcessTransactionRequest, Request}
import com.nubank.authorizer.domain.Authorizer
import com.nubank.authorizer.infrastructure.InMemoryAccountRepository
import io.circe.parser.decode
import cats.implicits._
import com.nubank.authorizer.application.cli.model.ConsoleResponse

class Program {
  type ParsingError[A] = Either[io.circe.Error, A]

  private val authorizer = new Authorizer(new InMemoryAccountRepository())

  def run(input: List[String], success: ConsoleResponse => Unit, fail: io.circe.Error => Unit) : Unit = {
    val authorizations = input.map(decode[Request]).nested.map {
      case car: CreateAccountRequest => authorizer.createAccount(car.asModel)
      case ptr: ProcessTransactionRequest => authorizer.authorize(ptr.asModel)
    }

    authorizations.map(ConsoleResponse.from).value.foreach {
      case Left(err) => fail(err)
      case Right(response) => success(response)
    }
  }
}
