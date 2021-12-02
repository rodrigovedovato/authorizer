package com.nubank.authorizer

case class Account private (activeCard: Boolean, availableLimit: Int)

object Account {
  def empty(): Account = Account(activeCard = false, 0)

  def active(availableLimit: Int): Account = Account(activeCard = true, availableLimit)
  def inactive(availableLimit: Int): Account = Account(activeCard = false, availableLimit)
}
