package com.nubank.authorizer

import com.nubank.authorizer.Authorizer.messages.CreateAccountRequest
import org.scalatest.wordspec.AnyWordSpec

class AuthorizerSpec extends AnyWordSpec {
  "The authorizer" when {
    val authorizer = new Authorizer()

    "asked to create a new account" should {
      "return it" in {
        val accountState = authorizer.send(
          state = AccountState.empty(),
          message = CreateAccountRequest(activeCard = true, availableLimit = 5)
        )

        assert(accountState.account.activeCard)
        assertResult(5)(accountState.account.availableLimit)
        assertResult(0)(accountState.violations.length)
      }
    }
  }
}
