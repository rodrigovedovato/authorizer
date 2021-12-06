package com.nubank.authorizer.application.cli.model

import com.nubank.authorizer.application.cli.model.json._
import com.nubank.authorizer.domain.model.AuthorizerMessages.{CreateAccountMessage, ProcessTransactionMessage}
import io.circe.generic.extras.ConfiguredJsonCodec

object requests {
  sealed trait Request

  @ConfiguredJsonCodec final case class CreateAccountRequest(account: ConsoleAccount) extends Request {
    def asModel : CreateAccountMessage = {
      CreateAccountMessage(account.activeCard, account.availableLimit)
    }
  }
  @ConfiguredJsonCodec final case class ProcessTransactionRequest(transaction: ConsoleTransaction) extends Request {
    def asModel: ProcessTransactionMessage = {
      ProcessTransactionMessage(transaction.to)
    }
  }
}
