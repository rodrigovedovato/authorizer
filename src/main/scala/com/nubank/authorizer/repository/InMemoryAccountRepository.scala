package com.nubank.authorizer.repository

import com.nubank.authorizer.Account
import com.nubank.authorizer.LanguageExtensions.EitherExtensions

class InMemoryAccountRepository extends AccountRepository {
  private var account: Account = _

  override def get : Option[Account] = Option(account)

  override def update(acc: Account): Either[AccountNotInitialized.type, Account] = {
    Either.cond(account != null, acc, AccountNotInitialized).tapRight { _ =>
      account = acc
    }
  }

  override def save(acc: Account): Either[AccountAlreadyInitialized, Account] = {
    Either.cond(account == null, acc, AccountAlreadyInitialized(account)).tapRight { a =>
      account = a
    }
  }
}
