package com.nubank.authorizer.rules
import com.nubank.authorizer.AccountState
import com.nubank.authorizer.AccountState.{DoubleTransaction, HighFrequencySmallInterval, Violation}
import com.nubank.authorizer.Authorizer.messages
import com.nubank.authorizer.Window.errors.{DuplicateAndOverflow, DuplicateEntry, InsertionError, WindowOverflow}

class TransactionWindowRule extends BaseRule {
  private val errorToViolation: InsertionError => List[Violation] = {
    case WindowOverflow => List(HighFrequencySmallInterval)
    case DuplicateEntry => List(DoubleTransaction)
    case DuplicateAndOverflow => List(HighFrequencySmallInterval, DoubleTransaction)
  }

  override def check(state: AccountState, ptm: messages.ProcessTransactionMessage): AccountState = {
    state.account.get
      .addTransaction(ptm.transaction)
      .left
      .map(errorToViolation)
      .fold(v => state.copy(violations = state.violations ++ v),{ account =>
          state.copy(account = Some(account))
      })
  }
}
