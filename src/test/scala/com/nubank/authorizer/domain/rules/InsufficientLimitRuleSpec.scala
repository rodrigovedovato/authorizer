package com.nubank.authorizer.domain.rules

import com.nubank.authorizer.domain.Authorizer.messages.ProcessTransactionMessage
import com.nubank.authorizer.domain.model
import com.nubank.authorizer.domain.model.Authorization.InsufficientLimit
import com.nubank.authorizer.domain.model.{Account, Authorization, Transaction}
import org.scalatest.wordspec.AnyWordSpec

import java.time.OffsetDateTime

class InsufficientLimitRuleSpec extends AnyWordSpec {
  "The card-not-active rule" should {
    val rule = new InsufficientLimitRule()

    "be triggered" in {
      val result = rule.check(
        authorization = Authorization(Account.create(true, 50), List.empty),
        ptm = ProcessTransactionMessage(Transaction(merchant = "The Blue Pub", amount = 100, time = OffsetDateTime.now()))
      )

      assert(result.violations.last == InsufficientLimit)
    }
    "not be triggered" in {
      val result = rule.check(
        authorization = Authorization(Account.create(true, 1000), List.empty),
        ptm = ProcessTransactionMessage(model.Transaction(merchant = "The Blue Pub", amount = 20, time = OffsetDateTime.now()))
      )

      assert(result.violations.isEmpty)
    }
  }
}
