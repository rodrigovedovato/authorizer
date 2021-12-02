package com.nubank.authorizer

import com.nubank.authorizer.AccountState.AccountAlreadyInitialized
import com.nubank.authorizer.Authorizer.messages.{AuthorizerMessage, CreateAccountRequest}

class Authorizer {
  def send (state: AccountState, message: AuthorizerMessage) : AccountState = {
    message match {
      case req: CreateAccountRequest => createAccount(state, req)
    }
  }

  def createAccount(accountState: AccountState, accountRequest: CreateAccountRequest) : AccountState = {
    if (accountState.initialized) {
      accountState.copy(violations = accountState.violations :+ AccountAlreadyInitialized)
    } else {
      AccountState(
        initialized = true,
        account = Account.create(accountRequest.activeCard, accountRequest.availableLimit),
        violations = List.empty
      )
    }
  }
}

object Authorizer {
  object messages {
    sealed trait AuthorizerMessage
    final case class CreateAccountRequest(activeCard: Boolean, availableLimit: Int) extends AuthorizerMessage
  }
}
