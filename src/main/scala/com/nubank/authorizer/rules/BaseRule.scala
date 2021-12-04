package com.nubank.authorizer.rules

import com.nubank.authorizer.Authorization
import com.nubank.authorizer.Authorizer.messages.ProcessTransactionMessage

abstract class BaseRule {
  def check(authorization: Authorization, ptm: ProcessTransactionMessage): Authorization
}
