package com.nubank.authorizer.rules

import com.nubank.authorizer.Authorization.{DoubleTransaction, HighFrequencySmallInterval}
import com.nubank.authorizer.{Account, Authorization, Transaction}
import com.nubank.authorizer.Authorizer.messages.ProcessTransactionMessage
import org.scalatest.wordspec.AnyWordSpec

import java.time.OffsetDateTime

class TransactionWindowRuleSpec extends AnyWordSpec {
  val rule = new TransactionWindowRule()

  "The high-frequency-small-interval rule" should {
    "be triggered" in {
      val auth1: Authorization = rule.check(
        authorization = Authorization(Account.create(true, 10000), List.empty),
        ptm = ProcessTransactionMessage(
          Transaction(
            merchant = "The Blue Pub",
            amount = 20,
            time = OffsetDateTime.now()
          )
        )
      )

      val state2: Authorization = rule.check(
        authorization = auth1,
        ptm = ProcessTransactionMessage(
          Transaction(
            merchant = "O'Malleys Pub",
            amount = 20,
            time = OffsetDateTime.now().plusSeconds(30)
          )
        )
      )

      val state3: Authorization = rule.check(
        authorization = state2,
        ptm = ProcessTransactionMessage(
          Transaction(
            merchant = "Deep Bar 611",
            amount = 20,
            time = OffsetDateTime.now().plusSeconds(45)
          )
        )
      )

      val state4: Authorization = rule.check(
        authorization = state3,
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
      val state1: Authorization = rule.check(
        authorization = Authorization(Account.create(true, 10000), List.empty),
        ptm = ProcessTransactionMessage(
          Transaction(
            merchant = "The Blue Pub",
            amount = 20,
            time = OffsetDateTime.now()
          )
        )
      )

      val state2: Authorization = rule.check(
        authorization = state1,
        ptm = ProcessTransactionMessage(
          Transaction(
            merchant = "O'Malleys Pub",
            amount = 20,
            time = OffsetDateTime.now().plusSeconds(30)
          )
        )
      )

      val state3: Authorization = rule.check(
        authorization = state2,
        ptm = ProcessTransactionMessage(
          Transaction(
            merchant = "Deep Bar 611",
            amount = 20,
            time = OffsetDateTime.now().plusSeconds(45)
          )
        )
      )

      val state4: Authorization = rule.check(
        authorization = state3,
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
      val state1: Authorization = rule.check(
        authorization = Authorization(Account.create(true, 10000), List.empty),
        ptm = ProcessTransactionMessage(
          Transaction(
            merchant = "The Blue Pub",
            amount = 20,
            time = OffsetDateTime.now()
          )
        )
      )

      val state2: Authorization = rule.check(
        authorization = state1,
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
      val state1: Authorization = rule.check(
        authorization = Authorization(Account.create(true, 10000), List.empty),
        ptm = ProcessTransactionMessage(
          Transaction(
            merchant = "The Blue Pub",
            amount = 20,
            time = OffsetDateTime.now()
          )
        )
      )

      val state2: Authorization = rule.check(
        authorization = state1,
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

  "Both double-transaction and high-frequency rules" should {
    "be triggered" in {
      val state1: Authorization = rule.check(
        authorization = Authorization(Account.create(true, 10000), List.empty),
        ptm = ProcessTransactionMessage(
          Transaction(
            merchant = "The Blue Pub",
            amount = 20,
            time = OffsetDateTime.now()
          )
        )
      )

      val state2: Authorization = rule.check(
        authorization = state1,
        ptm = ProcessTransactionMessage(
          Transaction(
            merchant = "Bar do Moe",
            amount = 20,
            time = OffsetDateTime.now().plusSeconds(30)
          )
        )
      )

      val state3: Authorization = rule.check(
        authorization = state2,
        ptm = ProcessTransactionMessage(
          Transaction(
            merchant = "Valadares",
            amount = 20,
            time = OffsetDateTime.now().plusSeconds(45)
          )
        )
      )

      val state4: Authorization = rule.check(
        authorization = state3,
        ptm = ProcessTransactionMessage(
          Transaction(
            merchant = "Valadares",
            amount = 20,
            time = OffsetDateTime.now().plusSeconds(60)
          )
        )
      )

      assert(state4.violations == List(HighFrequencySmallInterval, DoubleTransaction))
    }
  }
}
