package com.nubank.authorizer

import com.nubank.authorizer.Authorization.{AccountAlreadyInitialized, AccountNotInitialized}
import com.nubank.authorizer.Authorizer.messages.{CreateAccountMessage, ProcessTransactionMessage}
import org.scalatest.wordspec.AnyWordSpec

import java.time.OffsetDateTime

class AuthorizerSpec extends AnyWordSpec {
  "The authorizer" when {
    "asked to create a new account" should {
      val subject = new Authorizer()

      "return it" in {
        val accountState = subject.createAccount(CreateAccountMessage(activeCard = true, availableLimit = 5))

        assert(accountState.account.activeCard)
        assertResult(5)(accountState.account.availableLimit)
        assertResult(0)(accountState.violations.length)
      }
      "violate the account-already-initialized rule" in {
        val before = subject.createAccount(CreateAccountMessage(activeCard = true, availableLimit = 5))
        val after = subject.createAccount(CreateAccountMessage(activeCard = false, availableLimit = 100))

        assert(after.account.activeCard)
        assert(after.account.availableLimit == 5)
        assert(after.violations.last == AccountAlreadyInitialized)
      }
    }

    "asked to process a transaction" should {
      "return a account-not-initialized violation" in {
        val subject = new Authorizer()
        val after = subject.authorize(ProcessTransactionMessage(Transaction(merchant = "The Blue Pub", amount = 20, time = OffsetDateTime.now())))

        assert(after.violations.last == AccountNotInitialized)
      }
      "process it and remove the amount from the account" in {
        val subject = new Authorizer()

        subject.createAccount(CreateAccountMessage(activeCard = true, availableLimit = 100))
        val after = subject.authorize(ProcessTransactionMessage(Transaction(merchant = "The Blue Pub", amount = 20, time = OffsetDateTime.now())))

        assert(after.account.availableLimit == 80)
        assert(after.violations.isEmpty)
      }
      "not remove the amount from the limit when there is a violation" in {
        val subject = new Authorizer()

        subject.createAccount(CreateAccountMessage(activeCard = true, availableLimit = 100))
        val after = subject.authorize(ProcessTransactionMessage(Transaction(merchant = "The Blue Pub", amount = 200, time = OffsetDateTime.now())))

        assert(after.account.availableLimit == 100)
        assert(after.violations.size == 1)
      }
    }
  }
}
