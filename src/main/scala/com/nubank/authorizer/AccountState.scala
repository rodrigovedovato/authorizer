package com.nubank.authorizer

import com.nubank.authorizer.AccountState.Violation

final case class AccountState(account: Option[Account], violations: List[Violation])

object AccountState {
  sealed trait Violation
  case object CardNotActive extends Violation
  case object DoubleTransaction extends Violation
  case object InsufficientLimit extends Violation
  case object AccountNotInitialized extends Violation
  case object AccountAlreadyInitialized extends Violation
  case object HighFrequencySmallInterval extends Violation

  def empty(): AccountState = AccountState(Option.empty, List.empty)
}



