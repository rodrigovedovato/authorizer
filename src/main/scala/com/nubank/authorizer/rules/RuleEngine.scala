package com.nubank.authorizer.rules

import com.nubank.authorizer.Authorization
import com.nubank.authorizer.Authorizer.messages.ProcessTransactionMessage

object RuleEngine {
  private val rules = List(
    new CardNotActiveRule(),
    new InsufficientLimitRule(),
    new TransactionWindowRule()
  )

  def execute(authorization: Authorization, ptm: ProcessTransactionMessage): Authorization = {
    rules.foldLeft(authorization)((authorization, rule) => rule.check(authorization, ptm))
  }
}
