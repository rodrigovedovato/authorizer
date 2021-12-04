package com.nubank.authorizer.domain.rules

import com.nubank.authorizer.domain.model.Authorization
import com.nubank.authorizer.domain.model.AuthorizerMessages.ProcessTransactionMessage

abstract class BaseRule {
  def check(authorization: Authorization, ptm: ProcessTransactionMessage): Authorization
}
