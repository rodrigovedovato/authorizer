package com.nubank.authorizer

import com.nubank.authorizer.Authorization.{AccountAlreadyInitialized, AccountNotInitialized}
import com.nubank.authorizer.Authorizer.messages.{CreateAccountMessage, ProcessTransactionMessage}
import com.nubank.authorizer.repository.AccountRepository
import org.scalatest.wordspec.AnyWordSpec

import java.time.OffsetDateTime

class AuthorizerSpec extends AnyWordSpec {
  "The authorizer" when {
    "asked to process a transaction" should {
      "return a account-not-initialized violation" in {
        val subject = new Authorizer(new AccountRepository {
          override def get: Option[Account] = Option.empty
          override def save(acc: Account): Either[repository.AccountAlreadyInitialized, Account] = ???
          override def update(acc: Account): Either[repository.AccountNotInitialized.type, Account] = ???
        })

        val after = subject.authorize(ProcessTransactionMessage(Transaction(merchant = "The Blue Pub", amount = 20, time = OffsetDateTime.now())))

        assert(after.violations.last == AccountNotInitialized)
      }
      "process it and remove the amount from the account" in {
        val subject = new Authorizer(new AccountRepository {
          override def get: Option[Account] = Some(Account.create(true, 100))
          override def save(acc: Account): Either[repository.AccountAlreadyInitialized, Account] = ???
          override def update(acc: Account): Either[repository.AccountNotInitialized.type, Account] = Right(acc)
        })

        val after = subject.authorize(ProcessTransactionMessage(Transaction(merchant = "The Blue Pub", amount = 20, time = OffsetDateTime.now())))

        assert(after.account.availableLimit == 80)
        assert(after.violations.isEmpty)
      }
      "not remove the amount from the limit when there is a violation" in {
        val subject = new Authorizer(new AccountRepository {
          override def get: Option[Account] = Some(Account.create(true, 100))
          override def save(acc: Account): Either[repository.AccountAlreadyInitialized, Account] = ???
          override def update(acc: Account): Either[repository.AccountNotInitialized.type, Account] = Right(acc)
        })

        val after = subject.authorize(ProcessTransactionMessage(Transaction(merchant = "The Blue Pub", amount = 200, time = OffsetDateTime.now())))

        assert(after.account.availableLimit == 100)
        assert(after.violations.size == 1)
      }
    }
  }
}
