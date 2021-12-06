package com.nubank.authorizer.infrastructure

import com.nubank.authorizer.LanguageExtensions.EitherExtensions
import com.nubank.authorizer.domain.model.Account
import com.nubank.authorizer.domain.repository.{AccountConflict, AccountNotFound, AccountRepository}

class InMemoryAccountRepository extends AccountRepository {
  private var account: Account = _

  override def get : Option[Account] = Option(account)

  override def update(acc: Account): Either[AccountNotFound.type, Account] = {
    Either.cond(account != null, acc, AccountNotFound).tapRight { _ =>
      account = acc
    }
  }

  override def save(acc: Account): Either[AccountConflict, Account] = {
    Either.cond(account == null, acc, AccountConflict(account)).tapRight { a =>
      account = a
    }
  }
}
