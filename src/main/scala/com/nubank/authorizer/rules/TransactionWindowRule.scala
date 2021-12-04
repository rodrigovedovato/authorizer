package com.nubank.authorizer.rules
import com.nubank.authorizer.AccountState
import com.nubank.authorizer.AccountState.{DoubleTransaction, HighFrequencySmallInterval, Violation}
import com.nubank.authorizer.Authorizer.messages
import com.nubank.authorizer.Window.errors.{DuplicateEntry, InsertionError, WindowOverflow}

class TransactionWindowRule(val nextRule: Option[BaseRule]) extends BaseRule(nextRule) {
  private val errorToViolation: InsertionError => Violation = {
    case WindowOverflow => HighFrequencySmallInterval
    case DuplicateEntry => DoubleTransaction
  }

  override def check(state: AccountState, ptm: messages.ProcessTransactionMessage): AccountState = {
    state.account.get
      .addTransaction(ptm.transaction)
      .left
      .map(errorToViolation)
      .fold(v => state.copy(violations = state.violations :+ v),{ account =>
          state.copy(account = Some(account))
      })
  }
}
