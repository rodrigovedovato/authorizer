package com.nubank.authorizer

final case class CreateAccountRequest(activeCard: Boolean, availableLimit: Int)
