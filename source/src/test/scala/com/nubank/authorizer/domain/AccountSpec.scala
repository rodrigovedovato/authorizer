package com.nubank.authorizer.domain

import com.nubank.authorizer.domain.model.Account
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
