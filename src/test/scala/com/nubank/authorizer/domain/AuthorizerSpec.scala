package com.nubank.authorizer.domain

import com.nubank.authorizer.domain.Authorizer.messages.{CreateAccountMessage, ProcessTransactionMessage}
import com.nubank.authorizer.domain.model.Authorization.{AccountAlreadyInitialized, AccountNotInitialized}
import com.nubank.authorizer.domain.model.{Account, Transaction}
import com.nubank.authorizer.domain.repository.AccountRepository
import com.nubank.authorizer.infrastructure.InMemoryAccountRepository
import org.scalatest.wordspec.AnyWordSpec

import java.time.OffsetDateTime

class AuthorizerSpec extends AnyWordSpec {
  "The authorizer" when {
    "asked to create an account" should {
      "create it" in {
        val subject = new Authorizer(new InMemoryAccountRepository())
        val caa = subject.createAccount(CreateAccountMessage(true, 100))

        assert(caa.violations.isEmpty)
        assert(caa.account.activeCard)
        assertResult(100)(caa.account.availableLimit)
      }
      "return a account-already-initialized violation" in {
        val subject = new Authorizer(new InMemoryAccountRepository())

        subject.createAccount(CreateAccountMessage(true, 100))
        val caa = subject.createAccount(CreateAccountMessage(true, 50))

        assert(caa.violations == List(AccountAlreadyInitialized))
      }
    }
    "asked to process a transaction" should {
      "return a account-not-initialized violation" in {
        val subject = new Authorizer(new AccountRepository {
          override def get: Option[Account] = Option.empty
          override def save(acc: Account): Either[repository.AccountConflict, Account] = ???
          override def update(acc: Account): Either[repository.AccountNotFound.type, Account] = ???
        })

        val after = subject.authorize(ProcessTransactionMessage(Transaction(merchant = "The Blue Pub", amount = 20, time = OffsetDateTime.now())))

        assert(after.violations.last == AccountNotInitialized)
      }
      "process it and remove the amount from the account" in {
        val subject = new Authorizer(new AccountRepository {
          override def get: Option[Account] = Some(Account.create(true, 100))
          override def save(acc: Account): Either[repository.AccountConflict, Account] = ???
          override def update(acc: Account): Either[repository.AccountNotFound.type, Account] = Right(acc)
        })

        val after = subject.authorize(ProcessTransactionMessage(model.Transaction(merchant = "The Blue Pub", amount = 20, time = OffsetDateTime.now())))

        assert(after.account.availableLimit == 80)
        assert(after.violations.isEmpty)
      }
      "not remove the amount from the limit when there is a violation" in {
        val subject = new Authorizer(new AccountRepository {
          override def get: Option[Account] = Some(Account.create(true, 100))
          override def save(acc: Account): Either[repository.AccountConflict, Account] = ???
          override def update(acc: Account): Either[repository.AccountNotFound.type, Account] = Right(acc)
        })

        val after = subject.authorize(ProcessTransactionMessage(model.Transaction(merchant = "The Blue Pub", amount = 200, time = OffsetDateTime.now())))

        assert(after.account.availableLimit == 100)
        assert(after.violations.size == 1)
      }
    }
  }
}
