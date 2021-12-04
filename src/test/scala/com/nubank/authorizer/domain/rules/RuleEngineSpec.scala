package com.nubank.authorizer.domain.rules

import com.nubank.authorizer.domain.Authorizer.messages.ProcessTransactionMessage
import com.nubank.authorizer.domain.model
import com.nubank.authorizer.domain.model.Authorization.{DoubleTransaction, InsufficientLimit}
import com.nubank.authorizer.domain.model.{Account, Authorization, Transaction}
import org.scalatest.wordspec.AnyWordSpec

import java.time.OffsetDateTime

class RuleEngineSpec extends AnyWordSpec {
  "The rule engine" when {
    "asked to validate a transaction" should {
      "accumulate all violations" in {
        val fakeAccount = Account.create(true, 0).addTransaction(
          Transaction(merchant = "McDonald's", amount = 10, time = OffsetDateTime.now())
        )

        fakeAccount match {
          case Left(_) => assert(false)
          case Right(value) => {
            val authorization = Authorization(value, List.empty)
            val validated = RuleEngine.execute(authorization, ProcessTransactionMessage(
              model.Transaction(merchant = "McDonald's", amount = 10, time = OffsetDateTime.now())
            ))

            assert(validated.violations == List(InsufficientLimit, DoubleTransaction))
          }
        }
      }
    }
  }
}
