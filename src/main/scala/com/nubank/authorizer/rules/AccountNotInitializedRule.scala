package com.nubank.authorizer.rules

import com.nubank.authorizer.AccountState
import com.nubank.authorizer.AccountState.AccountNotInitialized
import com.nubank.authorizer.Authorizer.messages.ProcessTransactionMessage

private[rules] class AccountNotInitializedRule extends BaseRule(Option.empty) {
  override def check(state: AccountState, ptm: ProcessTransactionMessage): AccountState = {
    if (state.account.isEmpty) {
      state.copy(violations = state.violations :+ AccountNotInitialized)
    } else {
      doNext(state, ptm)
    }
  }
}
