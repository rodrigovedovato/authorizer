package com.nubank.authorizer.rules

import com.nubank.authorizer.AccountState
import com.nubank.authorizer.Authorizer.messages.ProcessTransactionMessage

object RuleEngine {
  private val rules = List(
    new TransactionWindowRule(),
    new InsufficientLimitRule(),
    new CardNotActiveRule(),
    new AccountNotInitializedRule()
  )

  def execute(accountState: AccountState, ptm: ProcessTransactionMessage): AccountState = {
    rules.foldRight(accountState)((rule, state) => rule.check(state, ptm))
  }
}
