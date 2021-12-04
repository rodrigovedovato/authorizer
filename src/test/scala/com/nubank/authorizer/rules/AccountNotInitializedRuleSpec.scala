package com.nubank.authorizer.rules

import com.nubank.authorizer.AccountState.AccountNotInitialized
import com.nubank.authorizer.Authorizer.messages.ProcessTransactionMessage
import com.nubank.authorizer.{Account, AccountState, Transaction}
import org.scalatest.wordspec.AnyWordSpec

import java.time.OffsetDateTime

class AccountNotInitializedRuleSpec extends AnyWordSpec {
  "The account-not-initialized rule" should {
    val rule = new AccountNotInitializedRule()

    "be triggered" in {
      val result : AccountState = rule.check(
        state = AccountState.empty(),
        ptm = ProcessTransactionMessage(Transaction(merchant = "The Blue Pub", amount = 20, time = OffsetDateTime.now()))
      )

      assert(result.violations.last == AccountNotInitialized)
    }
    "not be triggered" in {
      val result: AccountState = rule.check(
        state = AccountState(Some(Account.create(true, 100)), List.empty),
        ptm = ProcessTransactionMessage(Transaction(merchant = "The Blue Pub", amount = 20, time = OffsetDateTime.now()))
      )

      assert(result.violations.isEmpty)
    }
  }
}
