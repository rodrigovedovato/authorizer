package com.nubank.authorizer

class Account private (val activeCard: Boolean, val availableLimit: Int)

object Account {
  def empty(): Account = new Account(activeCard = false, availableLimit = 0)

  def create(activeCard: Boolean, availableLimit: Int): Account = new Account(activeCard, availableLimit)
}
