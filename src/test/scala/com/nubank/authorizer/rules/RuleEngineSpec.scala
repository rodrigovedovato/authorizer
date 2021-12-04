package com.nubank.authorizer.rules

import com.nubank.authorizer.Authorization.{DoubleTransaction, HighFrequencySmallInterval, InsufficientLimit}
import com.nubank.authorizer.{Authorization, Authorizer, Transaction}
import com.nubank.authorizer.Authorizer.messages.{CreateAccountMessage, ProcessTransactionMessage}
import org.scalatest.wordspec.AnyWordSpec

import java.time.OffsetDateTime

class RuleEngineSpec extends AnyWordSpec {
  "A transaction" when {
    val subject = new Authorizer()

    "that violates multiple rules" should {
      "accumulate all of them" in {
        val before = subject.createAccount(CreateAccountMessage(activeCard = true, availableLimit = 100))

        subject.authorize(
          ProcessTransactionMessage(
            Transaction(merchant = "McDonald's", amount = 10, time = OffsetDateTime.now())
          )
        )

        subject.authorize(
          ProcessTransactionMessage(
            Transaction(
              merchant = "Burger King",
              amount = 20,
              time = OffsetDateTime.now().plusSeconds(1)
            )
          )
        )

        subject.authorize(
          ProcessTransactionMessage(
            Transaction(
              merchant = "Burger King",
              amount = 5,
              time = OffsetDateTime.now().plusSeconds(5)
            )
          )
        )

        val after = subject.authorize(
          ProcessTransactionMessage(
            Transaction(
              merchant = "Lanchonete da Cidade",
              amount = 75,
              time = OffsetDateTime.now().plusSeconds(6)
            )
          )
        )

        assert(after.violations == List(InsufficientLimit, HighFrequencySmallInterval))
      }
    }
  }
}
