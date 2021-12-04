package com.nubank.authorizer.rules

import com.nubank.authorizer.AccountState.CardNotActive
import com.nubank.authorizer.Authorizer.messages.ProcessTransactionMessage
import com.nubank.authorizer.{Account, AccountState, Transaction}
import org.scalatest.wordspec.AnyWordSpec

import java.time.OffsetDateTime

class CardNotActiveRuleSpec extends AnyWordSpec {
  "The card-not-active rule" should {
    val rule = new CardNotActiveRule(Option.empty)

    "be triggered" in {
      val result = rule.check(
        state = AccountState(Some(Account.create(false, 1000)), List.empty),
        ptm = ProcessTransactionMessage(Transaction(merchant = "The Blue Pub", amount = 20, time = OffsetDateTime.now()))
      )

      assert(result.violations.last == CardNotActive)
    }
    "not be triggered" in {
      val result = rule.check(
        state = AccountState(Some(Account.create(true, 1000)), List.empty),
        ptm = ProcessTransactionMessage(Transaction(merchant = "The Blue Pub", amount = 20, time = OffsetDateTime.now()))
      )

      assert(result.violations.isEmpty)
    }
  }
}
