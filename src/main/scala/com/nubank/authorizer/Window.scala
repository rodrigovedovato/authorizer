package com.nubank.authorizer

import com.nubank.authorizer.Window.errors.{DuplicateAndOverflow, DuplicateEntry, InsertionError, WindowOverflow}

class Window private (interval: Int, size: Int, items: List[Transaction]) {
  val data: Set[Transaction] = items.toSet

  def copy(interval : Int = this.interval, size : Int = this.size, items : List[Transaction] = this.items): Window = {
    new Window(interval, size, items)
  }

  def add(transaction: Transaction) : Either[InsertionError, Window] = {
    val insideInterval = items.nonEmpty && transaction.timeDiff(items.head) <= interval
    val isDuplicate = items.nonEmpty && insideInterval && data.contains(transaction)
    val overflows  = items.nonEmpty && insideInterval && items.length == size

    case class Result(isEmpty: Boolean, insideInterval: Boolean, isDuplicate: Boolean, overflows: Boolean)

    Result(items.isEmpty, insideInterval, isDuplicate, overflows) match {
      case Result(true, false, false, false) => Right(copy(items = List(transaction)))
      case Result(false, false, false, false) => Right(copy(items = List(transaction)))
      case Result(false, true, false, false) => Right(copy(items = transaction :: items))
      case Result(false, true, true, false) => Left(DuplicateEntry)
      case Result(false, true, false, true) => Left(WindowOverflow)
      case Result(false, true, true, true) => Left(DuplicateAndOverflow)
    }
  }

  def count: Int = items.length
}

object Window {
  object errors {
    sealed trait InsertionError
    case object DuplicateEntry extends InsertionError
    case object WindowOverflow extends InsertionError
    case object DuplicateAndOverflow extends InsertionError
  }

  def apply(interval: Int, size: Int) = new Window(interval, size, List.empty)
}