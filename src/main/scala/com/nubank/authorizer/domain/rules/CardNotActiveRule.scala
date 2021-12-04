package com.nubank.authorizer.domain.rules

import com.nubank.authorizer.domain.model.Authorization
import com.nubank.authorizer.domain.model.Authorization.CardNotActive
import com.nubank.authorizer.domain.model.AuthorizerMessages.ProcessTransactionMessage

private[rules] class CardNotActiveRule extends BaseRule {
  override def check(authorization: Authorization, ptm: ProcessTransactionMessage): Authorization = {
    if (!authorization.account.activeCard) {
      authorization.copy(violations = authorization.violations :+ CardNotActive)
    } else {
      authorization
    }
  }
}
