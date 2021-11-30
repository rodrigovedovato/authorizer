package com.nubank.authorizer.common

import org.apache.commons.lang3.builder.HashCodeBuilder

import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

final case class Transaction(
    merchant: String,
    amount: Int,
    time: OffsetDateTime
) {

  /** * Returns the difference in minutes of two transactions
    * @param other
    * @return
    */
  def timeDiff(other: Transaction): Long = {
    ChronoUnit.MINUTES.between(other.time, time)
  }

  override def equals(obj: Any): Boolean = obj match {
    case that: Transaction =>
      merchant.equalsIgnoreCase(that.merchant) && amount == that.amount
    case _ => false
  }

  override def hashCode(): Int = {
    new HashCodeBuilder(23, 37).append(merchant).append(amount).toHashCode
  }
}
