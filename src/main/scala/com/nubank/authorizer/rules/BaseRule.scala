package com.nubank.authorizer.rules

import com.nubank.authorizer.AccountState
import com.nubank.authorizer.Authorizer.messages.ProcessTransactionMessage

abstract class BaseRule(val next: Option[BaseRule]) {
  def check(state: AccountState, ptm: ProcessTransactionMessage): AccountState

  protected def doNext(state: AccountState, ptm: ProcessTransactionMessage): AccountState = {
    if (next.isEmpty) {
      state
    } else {
      next.get.check(state, ptm)
    }
  }
}
