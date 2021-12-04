package com.nubank.authorizer

import com.nubank.authorizer.Authorization.AccountNotInitialized
import com.nubank.authorizer.Authorizer.messages.{CreateAccountMessage, ProcessTransactionMessage}
import com.nubank.authorizer.LanguageExtensions.EitherExtensions
import com.nubank.authorizer.rules.RuleEngine
import com.nubank.authorizer.repository.AccountRepository

class Authorizer(repository: AccountRepository) {
  def authorize(ptm: ProcessTransactionMessage): Authorization = {
    val authorization = repository.get.map(account => Authorization(account, List.empty)).map { auth =>
      val validated = RuleEngine.execute(auth, ptm)

      if (validated.violations.isEmpty) {
        validated.copy(
          account = validated.account.subtract(ptm.transaction.amount)
        )
      } else {
        validated
      }
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

object Authorizer {
  object messages {
    sealed trait AuthorizerMessage
    final case class CreateAccountMessage(activeCard: Boolean, availableLimit: Int) extends AuthorizerMessage
    final case class ProcessTransactionMessage(transaction: Transaction) extends AuthorizerMessage
  }
}
