package com.nubank.authorizer

import com.nubank.authorizer.Authorizer.Authorization
import org.scalatest.wordspec.AnyWordSpec

class AuthorizerSpec extends AnyWordSpec {
  "The authorizer" when {
    val authorizer = new Authorizer()

    "asked to create a new account" should {
      "return it" in {
        val authorization = authorizer.send(CreateAccountRequest(activeCard = true, availableLimit = 5))

        assert(authorization.account.activeCard)
        assertResult(5)(authorization.account.availableLimit)
        assertResult(0)(authorization.violations.length)
      }
    }
  }
}
