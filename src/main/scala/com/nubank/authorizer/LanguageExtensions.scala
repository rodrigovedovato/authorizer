package com.nubank.authorizer

object LanguageExtensions {
  implicit class EitherExtensions[E, T](val e: Either[E, T]) extends AnyVal {
    def tapRight(f: T => Unit) : Either[E, T] = {
      e match {
        case Left(_) => e
        case Right(v) => f(v); e
      }
    }

    def leftMap[EE](f: E => EE): Either[EE, T] = {
      e.left.map(f)
    }
  }
}
