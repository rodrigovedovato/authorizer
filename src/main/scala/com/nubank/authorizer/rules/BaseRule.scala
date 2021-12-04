package com.nubank.authorizer.rules

import com.nubank.authorizer.AccountState
import com.nubank.authorizer.Authorizer.messages.ProcessTransactionMessage

abstract class BaseRule {
  def check(state: AccountState, ptm: ProcessTransactionMessage): AccountState
}
