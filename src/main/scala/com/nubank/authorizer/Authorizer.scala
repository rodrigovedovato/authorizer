package com.nubank.authorizer

import com.nubank.authorizer.AccountState.AccountAlreadyInitialized
import com.nubank.authorizer.Authorizer.messages.{AuthorizerMessage, CreateAccountMessage, ProcessTransactionMessage}

class Authorizer {
  def send(state: AccountState, message: AuthorizerMessage) : AccountState = {
    message match {
      case cam: CreateAccountMessage => createAccount(state, cam)
      case ptm: ProcessTransactionMessage => processTransaction(state, ptm)
    }
  }

  def processTransaction(state: AccountState, ptm: ProcessTransactionMessage): AccountState = {
    state.copy(
      account = state.account.map(_.subtract(ptm.transaction.amount))
    )
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
