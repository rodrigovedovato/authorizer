package com.nubank.authorizer.domain.model

object AuthorizerMessages {
  sealed trait AuthorizerMessage
  final case class CreateAccountMessage(activeCard: Boolean, availableLimit: Int) extends AuthorizerMessage
  final case class ProcessTransactionMessage(transaction: Transaction) extends AuthorizerMessage
}
