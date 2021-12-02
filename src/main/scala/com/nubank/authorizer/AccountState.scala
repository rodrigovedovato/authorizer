package com.nubank.authorizer

import com.nubank.authorizer.AccountState.Violation

final case class AccountState(initialized: Boolean, account: Account, violations: List[Violation])

object AccountState {
  sealed trait Violation
  case object AccountAlreadyInitialized extends Violation

  def empty(): AccountState = AccountState(initialized = false, Account.empty(), List.empty)
}



