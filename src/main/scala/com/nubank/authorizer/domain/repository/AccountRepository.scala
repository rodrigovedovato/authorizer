package com.nubank.authorizer.domain.repository

import com.nubank.authorizer.domain.model.Account

trait AccountRepository {
  def get: Option[Account]
  def save(acc: Account): Either[AccountConflict, Account]
  def update(acc: Account): Either[AccountNotFound.type, Account]
}
