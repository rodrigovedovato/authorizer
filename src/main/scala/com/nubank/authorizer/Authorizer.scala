package com.nubank.authorizer

import com.nubank.authorizer.AccountState.AccountAlreadyInitialized
import com.nubank.authorizer.Authorizer.messages.{AuthorizerMessage, CreateAccountMessage, ProcessTransactionMessage}
import com.nubank.authorizer.rules.RuleEngine

class Authorizer {
  def send(state: AccountState, message: AuthorizerMessage) : AccountState = {
    message match {
      case cam: CreateAccountMessage => createAccount(state, cam)
      case ptm: ProcessTransactionMessage => processTransaction(state, ptm)
    }
  }

  def processTransaction(state: AccountState, ptm: ProcessTransactionMessage): AccountState = {
    val validated = RuleEngine.execute(state, ptm)

    if (validated.violations.isEmpty) {
      validated.copy(
        account = validated.account.map(_.subtract(ptm.transaction.amount))
      )
    } else {
      validated
    }
  }

  def createAccount(accountState: AccountState, accountRequest: CreateAccountMessage) : AccountState = {
    if (accountState.account.isDefined) {
      accountState.copy(violations = accountState.violations :+ AccountAlreadyInitialized)
    } else {
      AccountState(
        account = Some(Account.create(accountRequest.activeCard, accountRequest.availableLimit)),
        violations = List.empty
      )
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
