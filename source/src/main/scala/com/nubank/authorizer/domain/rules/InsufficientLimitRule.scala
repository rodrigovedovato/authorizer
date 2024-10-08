package com.nubank.authorizer.domain.rules

import com.nubank.authorizer.domain.model.Authorization
import com.nubank.authorizer.domain.model.Authorization.InsufficientLimit
import com.nubank.authorizer.domain.model.AuthorizerMessages.ProcessTransactionMessage

private[rules] class InsufficientLimitRule extends BaseRule {
  override def check(authorization: Authorization, ptm: ProcessTransactionMessage): Authorization = {
    if ((authorization.account.availableLimit - ptm.transaction.amount ) < 0) {
      authorization.copy(violations = authorization.violations :+ InsufficientLimit)
    } else {
      authorization
    }
  }
}
