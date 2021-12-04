package com.nubank.authorizer.rules
import com.nubank.authorizer.Authorization
import com.nubank.authorizer.Authorization.{DoubleTransaction, HighFrequencySmallInterval, Violation}
import com.nubank.authorizer.Authorizer.messages
import com.nubank.authorizer.Window.errors.{DuplicateAndOverflow, DuplicateEntry, InsertionError, WindowOverflow}

class TransactionWindowRule extends BaseRule {
  private val errorToViolation: InsertionError => List[Violation] = {
    case WindowOverflow => List(HighFrequencySmallInterval)
    case DuplicateEntry => List(DoubleTransaction)
    case DuplicateAndOverflow => List(HighFrequencySmallInterval, DoubleTransaction)
  }

  override def check(authorization: Authorization, ptm: messages.ProcessTransactionMessage): Authorization = {
    authorization
      .account
      .addTransaction(ptm.transaction)
      .left
      .map(errorToViolation)
      .fold(v => authorization.copy(violations = authorization.violations ++ v),{ account =>
          authorization.copy(account = account)
      })
  }
}
