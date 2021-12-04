package com.nubank.authorizer.rules
import com.nubank.authorizer.Authorization
import com.nubank.authorizer.Authorization.CardNotActive
import com.nubank.authorizer.Authorizer.messages

private[rules] class CardNotActiveRule extends BaseRule {
  override def check(authorization: Authorization, ptm: messages.ProcessTransactionMessage): Authorization = {
    if (!authorization.account.activeCard) {
      authorization.copy(violations = authorization.violations :+ CardNotActive)
    } else {
      authorization
    }
  }
}
