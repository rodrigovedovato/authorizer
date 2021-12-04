package com.nubank.authorizer

import com.nubank.authorizer.Authorization.{AccountAlreadyInitialized, AccountNotInitialized}
import com.nubank.authorizer.Authorizer.messages.{AuthorizerMessage, CreateAccountMessage, ProcessTransactionMessage}
import com.nubank.authorizer.rules.RuleEngine

import scala.util.chaining.scalaUtilChainingOps

class Authorizer {
  var account: Option[Account] = Option.empty

  def authorize(ptm: ProcessTransactionMessage): Authorization = {
    account.fold(Authorization(null, List(AccountNotInitialized))) { a =>
      val authorization = Authorization(a, List.empty)
      val validated = RuleEngine.execute(authorization, ptm)

      if (validated.violations.isEmpty) {
        validated.copy(
          account = validated.account.subtract(ptm.transaction.amount)
        )
      } else {
        validated
      }
    }.tap(a => account = Some(a.account))
  }

  def createAccount(accountRequest: CreateAccountMessage) : Authorization = {
    if (account.isEmpty) {
      val auth = Authorization.newAccount(accountRequest)
      account = Some(auth.account)
      auth
    } else {
      Authorization.alreadyInitialized(account.get)
    }
  }
}

object Authorizer {
  object messages {
    sealed trait AuthorizerMessage
    final case class CreateAccountMessage(activeCard: Boolean, availableLimit: Int) extends AuthorizerMessage
    final case class ProcessTransactionMessage(transaction: Transaction) extends AuthorizerMessage
  }
}
