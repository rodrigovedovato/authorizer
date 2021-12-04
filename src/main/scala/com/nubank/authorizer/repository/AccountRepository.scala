package com.nubank.authorizer.repository

import com.nubank.authorizer.Account

trait AccountRepository {
  def get : Option[Account]
  def save(acc: Account): Either[AccountAlreadyInitialized, Account]
  def update(acc: Account): Either[AccountNotInitialized.type, Account]
}
