package com.nubank.authorizer

import org.scalatest.wordspec.AnyWordSpec

class AccountSpec extends AnyWordSpec {
  "An account" when {
    val account : Account = Account.empty()

    "empty" should {
      "not have an active card" in {
        assertResult(false)(account.activeCard)
      }
      "not have any available limit" in {
        assertResult(0)(account.availableLimit)
      }
    }
  }
}
