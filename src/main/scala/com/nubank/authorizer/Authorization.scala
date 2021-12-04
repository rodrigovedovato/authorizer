package com.nubank.authorizer

import com.nubank.authorizer.Authorization.Violation
import com.nubank.authorizer.Authorizer.messages.CreateAccountMessage

final case class Authorization(account: Account, violations: List[Violation])

object Authorization {
  sealed trait Violation
  case object CardNotActive extends Violation
  case object DoubleTransaction extends Violation
  case object InsufficientLimit extends Violation
  case object AccountNotInitialized extends Violation
  case object AccountAlreadyInitialized extends Violation
  case object HighFrequencySmallInterval extends Violation

  def alreadyInitialized(previous: Account): Authorization = Authorization(previous, List(AccountAlreadyInitialized))

  def newAccount(createAccountMessage: CreateAccountMessage): Authorization = {
    Authorization(
      Account.create(createAccountMessage.activeCard, createAccountMessage.availableLimit), List.empty
    )
  }
}



