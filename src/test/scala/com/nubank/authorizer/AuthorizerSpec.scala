package com.nubank.authorizer

import com.nubank.authorizer.AccountState.AccountAlreadyInitialized
import com.nubank.authorizer.Authorizer.messages.{CreateAccountMessage, ProcessTransactionMessage}
import org.scalatest.wordspec.AnyWordSpec

import java.time.OffsetDateTime

class AuthorizerSpec extends AnyWordSpec {
  "The authorizer" when {
    val subject = new Authorizer()

    "asked to create a new account" should {
      "return it" in {
        val accountState = subject.send(AccountState.empty(),CreateAccountMessage(activeCard = true, availableLimit = 5))

        assert(accountState.account.activeCard)
        assertResult(5)(accountState.account.availableLimit)
        assertResult(0)(accountState.violations.length)
      }
      "violate the account-already-initialized rule" in {
        val before = subject.send(AccountState.empty(),CreateAccountMessage(activeCard = true, availableLimit = 5))
        val after = subject.send(before, CreateAccountMessage(activeCard = false, availableLimit = 100))

        assert(after.account.activeCard)
        assert(after.account.availableLimit == 5)
        assert(after.violations.last == AccountAlreadyInitialized)
      }
    }

    "asked to process a transaction" should {
      "process it and remove the amount from the account" in {
        val before = subject.send(AccountState.empty(), CreateAccountMessage(activeCard = true, availableLimit = 100))
        val after = subject.send(before, ProcessTransactionMessage(Transaction(merchant = "The Blue Pub", amount = 20, time = OffsetDateTime.now())))

        assert(after.account.availableLimit == 80)
        assert(after.violations.isEmpty)
      }
      "violate the account-not-initialized rule" in {
        assert(false)
      }
      "violate the card-not-active rule" in {
        assert(false)
      }
      "violate the insufficient-limit rule" in {
        assert(false)
      }
      "violate the frequency-small-interval rule" in {
        assert(false)
      }
      "violate the double-transaction rule" in {
        assert(false)
      }
    }
  }
}
