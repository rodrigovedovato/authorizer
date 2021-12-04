package com.nubank.authorizer.rules

import com.nubank.authorizer.AccountState
import com.nubank.authorizer.AccountState.AccountNotInitialized
import com.nubank.authorizer.Authorizer.messages.ProcessTransactionMessage

private[rules] class AccountNotInitializedRule(val nextRule: Option[BaseRule]) extends BaseRule(nextRule) {
  override def check(state: AccountState, ptm: ProcessTransactionMessage): AccountState = {
    if (state.account.isEmpty) {
      state.copy(violations = state.violations :+ AccountNotInitialized)
    } else {
      doNext(state, ptm)
    }
  }
}
