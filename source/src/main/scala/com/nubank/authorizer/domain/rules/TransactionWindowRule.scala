package com.nubank.authorizer.domain.rules

import com.nubank.authorizer.domain.Window.errors.{DuplicateAndOverflow, DuplicateEntry, InsertionError, WindowOverflow}
import com.nubank.authorizer.domain.model.Authorization
import com.nubank.authorizer.domain.model.Authorization.{DoubleTransaction, HighFrequencySmallInterval, Violation}
import com.nubank.authorizer.domain.model.AuthorizerMessages.ProcessTransactionMessage

class TransactionWindowRule extends BaseRule {
  private val errorToViolation: InsertionError => List[Violation] = {
    case WindowOverflow => List(HighFrequencySmallInterval)
    case DuplicateEntry => List(DoubleTransaction)
    case DuplicateAndOverflow => List(HighFrequencySmallInterval, DoubleTransaction)
  }

  override def check(authorization: Authorization, ptm: ProcessTransactionMessage): Authorization = {
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
