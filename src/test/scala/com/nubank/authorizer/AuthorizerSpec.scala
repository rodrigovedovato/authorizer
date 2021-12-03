package com.nubank.authorizer

import com.nubank.authorizer.AccountState.{AccountAlreadyInitialized, AccountNotInitialized}
import com.nubank.authorizer.Authorizer.messages.{CreateAccountMessage, ProcessTransactionMessage}
import org.scalatest.wordspec.AnyWordSpec

import java.time.OffsetDateTime

class AuthorizerSpec extends AnyWordSpec {
  "The authorizer" when {
    val subject = new Authorizer()

    "asked to create a new account" should {
      "return it" in {
        val accountState = subject.send(AccountState.empty(),CreateAccountMessage(activeCard = true, availableLimit = 5))

        assert(accountState.account.get.activeCard)
        assertResult(5)(accountState.account.get.availableLimit)
        assertResult(0)(accountState.violations.length)
      }
      "violate the account-already-initialized rule" in {
        val before = subject.send(AccountState.empty(),CreateAccountMessage(activeCard = true, availableLimit = 5))
        val after = subject.send(before, CreateAccountMessage(activeCard = false, availableLimit = 100))

        assert(after.account.get.activeCard)
        assert(after.account.get.availableLimit == 5)
        assert(after.violations.last == AccountAlreadyInitialized)
      }
    }

    "asked to process a transaction" should {
      "process it and remove the amount from the account" in {
        val before = subject.send(AccountState.empty(), CreateAccountMessage(activeCard = true, availableLimit = 100))
        val after = subject.send(before, ProcessTransactionMessage(Transaction(merchant = "The Blue Pub", amount = 20, time = OffsetDateTime.now())))

        assert(after.account.get.availableLimit == 80)
        assert(after.violations.isEmpty)
      }
      "violate the account-not-initialized rule" in {
        val before = AccountState.empty()
        val after = subject.send(before, ProcessTransactionMessage(Transaction(merchant = "The Blue Pub", amount = 20, time = OffsetDateTime.now())))

        assert(after.account.isEmpty)
        assert(after.violations == List(AccountNotInitialized))
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
