package com.nubank.authorizer.domain.rules

import com.nubank.authorizer.domain.Authorizer.messages.ProcessTransactionMessage
import com.nubank.authorizer.domain.model.Authorization

abstract class BaseRule {
  def check(authorization: Authorization, ptm: ProcessTransactionMessage): Authorization
}
