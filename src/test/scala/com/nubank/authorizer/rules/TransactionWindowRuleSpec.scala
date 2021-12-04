package com.nubank.authorizer.rules

import com.nubank.authorizer.AccountState.{DoubleTransaction, HighFrequencySmallInterval}
import com.nubank.authorizer.{Account, AccountState, Transaction}
import com.nubank.authorizer.Authorizer.messages.ProcessTransactionMessage
import org.scalatest.wordspec.AnyWordSpec

import java.time.OffsetDateTime

class TransactionWindowRuleSpec extends AnyWordSpec {
  val rule = new TransactionWindowRule(Option.empty)

  "The high-frequency-small-interval rule" should {
    "be triggered" in {
      val state1: AccountState = rule.check(
        state = AccountState(Some(Account.create(true, 10000)), List.empty),
        ptm = ProcessTransactionMessage(
          Transaction(
            merchant = "The Blue Pub",
            amount = 20,
            time = OffsetDateTime.now()
          )
        )
      )

      val state2: AccountState = rule.check(
        state = state1,
        ptm = ProcessTransactionMessage(
          Transaction(
            merchant = "O'Malleys Pub",
            amount = 20,
            time = OffsetDateTime.now().plusSeconds(30)
          )
        )
      )

      val state3: AccountState = rule.check(
        state = state2,
        ptm = ProcessTransactionMessage(
          Transaction(
            merchant = "Deep Bar 611",
            amount = 20,
            time = OffsetDateTime.now().plusSeconds(45)
          )
        )
      )

      val state4: AccountState = rule.check(
        state = state3,
        ptm = ProcessTransactionMessage(
          Transaction(
            merchant = "Republic Pub",
            amount = 20,
            time = OffsetDateTime.now().plusSeconds(55)
          )
        )
      )

      assert(state4.violations.last == HighFrequencySmallInterval)
    }
    "not be triggered" in {
      val state1: AccountState = rule.check(
        state = AccountState(Some(Account.create(true, 10000)), List.empty),
        ptm = ProcessTransactionMessage(
          Transaction(
            merchant = "The Blue Pub",
            amount = 20,
            time = OffsetDateTime.now()
          )
        )
      )

      val state2: AccountState = rule.check(
        state = state1,
        ptm = ProcessTransactionMessage(
          Transaction(
            merchant = "O'Malleys Pub",
            amount = 20,
            time = OffsetDateTime.now().plusSeconds(30)
          )
        )
      )

      val state3: AccountState = rule.check(
        state = state2,
        ptm = ProcessTransactionMessage(
          Transaction(
            merchant = "Deep Bar 611",
            amount = 20,
            time = OffsetDateTime.now().plusSeconds(45)
          )
        )
      )

      val state4: AccountState = rule.check(
        state = state3,
        ptm = ProcessTransactionMessage(
          Transaction(
            merchant = "Republic Pub",
            amount = 20,
            time = OffsetDateTime.now().plusMinutes(5)
          )
        )
      )

      assert(state4.violations.isEmpty)
    }
  }

  "The double-transaction rule" should {
    "be triggered" in {
      val state1: AccountState = rule.check(
        state = AccountState(Some(Account.create(true, 10000)), List.empty),
        ptm = ProcessTransactionMessage(
          Transaction(
            merchant = "The Blue Pub",
            amount = 20,
            time = OffsetDateTime.now()
          )
        )
      )

      val state2: AccountState = rule.check(
        state = state1,
        ptm = ProcessTransactionMessage(
          Transaction(
            merchant = "The Blue Pub",
            amount = 20,
            time = OffsetDateTime.now().plusSeconds(30)
          )
        )
      )

      assert(state2.violations.last == DoubleTransaction)
    }
    "not be triggered" in {
      val state1: AccountState = rule.check(
        state = AccountState(Some(Account.create(true, 10000)), List.empty),
        ptm = ProcessTransactionMessage(
          Transaction(
            merchant = "The Blue Pub",
            amount = 20,
            time = OffsetDateTime.now()
          )
        )
      )

      val state2: AccountState = rule.check(
        state = state1,
        ptm = ProcessTransactionMessage(
          Transaction(
            merchant = "The Blue Pub",
            amount = 20,
            time = OffsetDateTime.now().plusMinutes(5)
          )
        )
      )

      assert(state2.violations.isEmpty)
    }
  }
}
