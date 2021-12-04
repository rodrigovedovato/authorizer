package com.nubank.authorizer.domain.rules

import com.nubank.authorizer.domain.Authorizer.messages.ProcessTransactionMessage
import com.nubank.authorizer.domain.model.Authorization

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
