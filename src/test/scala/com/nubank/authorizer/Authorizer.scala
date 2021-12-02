package com.nubank.authorizer

class Authorizer {
  def send(request: CreateAccountRequest): Authorizer.Authorization = ???
}

object Authorizer {
  sealed trait Violation

  final case class Authorization(account: Account, violations: List[Violation])
}
