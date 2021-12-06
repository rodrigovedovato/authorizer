package com.nubank.authorizer.application.cli.model

import com.nubank.authorizer.domain.model.Account
import io.circe.generic.extras.ConfiguredJsonCodec

import com.nubank.authorizer.application.cli.model.json._

@ConfiguredJsonCodec final case class ConsoleAccount(activeCard: Boolean, availableLimit: Int) {
  def to: Account = {
    Account.create(activeCard, availableLimit)
  }
}

object ConsoleAccount {
  def from(account: Account): ConsoleAccount = {
    ConsoleAccount(account.activeCard, account.availableLimit)
  }
}
