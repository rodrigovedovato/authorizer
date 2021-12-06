package com.nubank.authorizer.domain

import com.nubank.authorizer.LanguageExtensions.EitherExtensions
import com.nubank.authorizer.domain.model.Authorization.AccountNotInitialized
import com.nubank.authorizer.domain.model.AuthorizerMessages.{CreateAccountMessage, ProcessTransactionMessage}
import com.nubank.authorizer.domain.model.{Account, Authorization, Transaction}
import com.nubank.authorizer.domain.repository.AccountRepository
import com.nubank.authorizer.domain.rules.RuleEngine

class Authorizer(repository: AccountRepository) {
  def authorize(ptm: ProcessTransactionMessage): Authorization = {
    val authorization = repository.get.map(account => model.Authorization(account, List.empty)).map { auth =>
      val validated = RuleEngine.execute(auth, ptm)

      if (validated.violations.isEmpty) {
        validated.copy(
          account = validated.account.subtract(ptm.transaction.amount)
        )
      } else {
        validated
      }
    }

    authorization.foreach { auth =>
      repository.update(auth.account)
    }

    authorization.getOrElse(Authorization(null, List(AccountNotInitialized)))
  }

  def createAccount(accountRequest: CreateAccountMessage) : Authorization = {
    val account = Account.create(accountRequest.activeCard, accountRequest.availableLimit)

    repository
      .save(account)
      .leftMap(_.currentAccount)
      .fold(Authorization.alreadyInitialized, Authorization.newAccount)
  }
}
