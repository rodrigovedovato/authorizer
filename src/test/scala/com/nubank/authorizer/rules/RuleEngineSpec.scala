package com.nubank.authorizer.rules

import com.nubank.authorizer.AccountState.{DoubleTransaction, HighFrequencySmallInterval, InsufficientLimit}
import com.nubank.authorizer.{AccountState, Authorizer, Transaction}
import com.nubank.authorizer.Authorizer.messages.{CreateAccountMessage, ProcessTransactionMessage}
import org.scalatest.wordspec.AnyWordSpec

import java.time.OffsetDateTime

class RuleEngineSpec extends AnyWordSpec {
  "A transaction" when {
    val subject = new Authorizer()

    "that violates multiple rules" should {
      "accumulate all of them" in {
        val before = subject.send(
          AccountState.empty(),
          CreateAccountMessage(activeCard = true, availableLimit = 100)
        )

        val state1 = subject.send(
          before,
          ProcessTransactionMessage(
            Transaction(merchant = "McDonald's", amount = 10, time = OffsetDateTime.now())
          )
        )

        val state2 = subject.send(
          state1,
          ProcessTransactionMessage(
            Transaction(
              merchant = "Burger King",
              amount = 20,
              time = OffsetDateTime.now().plusSeconds(1)
            )
          )
        )

        val state3 = subject.send(
          state2,
          ProcessTransactionMessage(
            Transaction(
              merchant = "Burger King",
              amount = 5,
              time = OffsetDateTime.now().plusSeconds(5)
            )
          )
        )

        val state4 = subject.send(
          state3,
          ProcessTransactionMessage(
            Transaction(
              merchant = "Lanchonete da Cidade",
              amount = 75,
              time = OffsetDateTime.now().plusSeconds(6)
            )
          )
        )

        assert(state4.violations == List(InsufficientLimit, HighFrequencySmallInterval))
      }
    }
  }
}
