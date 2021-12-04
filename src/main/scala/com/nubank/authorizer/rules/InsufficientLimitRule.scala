package com.nubank.authorizer.rules
import com.nubank.authorizer.AccountState
import com.nubank.authorizer.AccountState.InsufficientLimit
import com.nubank.authorizer.Authorizer.messages

private[rules] class InsufficientLimitRule extends BaseRule {
  override def check(state: AccountState, ptm: messages.ProcessTransactionMessage): AccountState = {
    if ((state.account.get.availableLimit - ptm.transaction.amount ) < 0) {
      state.copy(violations = state.violations :+ InsufficientLimit)
    } else {
      state
    }
  }
}
