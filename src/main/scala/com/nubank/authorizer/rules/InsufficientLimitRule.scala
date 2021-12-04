package com.nubank.authorizer.rules
import com.nubank.authorizer.Authorization
import com.nubank.authorizer.Authorization.InsufficientLimit
import com.nubank.authorizer.Authorizer.messages

private[rules] class InsufficientLimitRule extends BaseRule {
  override def check(authorization: Authorization, ptm: messages.ProcessTransactionMessage): Authorization = {
    if ((authorization.account.availableLimit - ptm.transaction.amount ) < 0) {
      authorization.copy(violations = authorization.violations :+ InsufficientLimit)
    } else {
      authorization
    }
  }
}
