package com.nubank.authorizer

import com.nubank.authorizer.AccountState.Violation

final case class AccountState(account: Account, violations: List[Violation])

object AccountState {
  sealed trait Violation

  def empty(): AccountState = AccountState(Account.empty(), List.empty)
}



