package com.nubank.authorizer.common

import com.nubank.authorizer.common.Window.errors._

class Window private (interval: Int, size: Int, items: List[Transaction]) {
  val data: Set[Transaction] = items.toSet

  def copy(interval : Int = this.interval, size : Int = this.size, items : List[Transaction] = this.items): Window = {
    new Window(interval, size, items)
  }

  def add(transaction: Transaction) : Either[InsertionError, Window] = {
    val isEmpty: Boolean = items.isEmpty
    val insideInterval: Boolean = items.nonEmpty && transaction.timeDiff(items.head) <= interval
    val isDuplicate: Boolean = items.nonEmpty && insideInterval && data.contains(transaction)
    val overflows : Boolean = items.nonEmpty && insideInterval && items.length == size

    case class Result(isEmpty: Boolean, insideInterval: Boolean, isDuplicate: Boolean, overflows: Boolean)

    Result(isEmpty, insideInterval, isDuplicate, overflows) match {
      case Result(true, false, false, false) => Right(copy(items = List(transaction)))
      case Result(false, false, false, false) => Right(copy(items = List(transaction)))
      case Result(false, true, false, false) => Right(copy(items = transaction :: items))
      case Result(false, true, true, false) => Left(DuplicateEntry)
      case Result(false, true, false, true) => Left(WindowOverflow)
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