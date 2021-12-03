package com.nubank.authorizer

import com.nubank.authorizer.Window.errors

class Account private (val activeCard: Boolean, val availableLimit: Int, val window: Window) {
  def addTransaction(transaction: Transaction): Either[errors.InsertionError, Account] = {
    window.add(transaction).map(w => new Account(activeCard, availableLimit, w))
  }

  def subtract(amount: Int) : Account = {
    new Account(activeCard, availableLimit - amount, window)
  }
}

object Account {
  def empty(): Account = new Account(activeCard = false, availableLimit = 0, Window(2, 3))
  def create(activeCard: Boolean, availableLimit: Int): Account = new Account(activeCard, availableLimit, Window(2, 3))
}
