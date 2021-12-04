package com.nubank.authorizer.rules
import com.nubank.authorizer.AccountState
import com.nubank.authorizer.AccountState.CardNotActive
import com.nubank.authorizer.Authorizer.messages

private[rules] class CardNotActiveRule(val nextRule: Option[BaseRule]) extends BaseRule(nextRule) {
  override def check(state: AccountState, ptm: messages.ProcessTransactionMessage): AccountState = {
    if (!state.account.get.activeCard) {
      state.copy(violations = state.violations :+ CardNotActive)
    } else {
      doNext(state, ptm)
    }
  }
}
