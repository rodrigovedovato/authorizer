package com.nubank.authorizer.application.cli.model

import com.nubank.authorizer.domain.model.Transaction

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import com.nubank.authorizer.application.cli.model.json._
import io.circe.generic.extras.ConfiguredJsonCodec

@ConfiguredJsonCodec case class ConsoleTransaction(merchant: String, amount: Int, time: String) {
  def to: Transaction = {
    Transaction(merchant, amount, OffsetDateTime.parse(time, ConsoleTransaction.formatter))
  }
}

object ConsoleTransaction {
  val formatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME

  def from(transaction: Transaction): ConsoleTransaction = {
    ConsoleTransaction(transaction.merchant, transaction.amount, transaction.time.format(formatter))
  }
}
