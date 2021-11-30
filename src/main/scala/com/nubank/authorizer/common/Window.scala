package com.nubank.authorizer.common

import com.nubank.authorizer.common.Window.errors._

class Window private (interval: Int, size: Int, items: List[Transaction]) {
  val data: Set[Transaction] = items.toSet

  def add(transaction: Transaction) : Either[InsertionError, Window] = {
    items.headOption match {
      case None => Right(new Window(interval, size, List(transaction)))
      case Some(latest) =>
        if (transaction.timeDiff(latest) > interval) {
          Right(new Window(interval, size, List(transaction)))
        } else {
          if (items.length < size) {
            if (data.contains(transaction)) {
              Left(DuplicateEntry)
            } else {
              Right(new Window(interval, size, transaction +: items))
            }
          } else {
            Left(WindowOverflow)
          }
        }
    }
  }

  def count: Int = items.length
}

object Window {
  object errors {
    sealed trait InsertionError
    case object DuplicateEntry extends InsertionError
    case object WindowOverflow extends InsertionError
  }

  def apply(interval: Int, size: Int) = new Window(interval, size, List.empty)
}