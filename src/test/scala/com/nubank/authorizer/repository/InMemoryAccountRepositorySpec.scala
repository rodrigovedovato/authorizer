package com.nubank.authorizer.repository

import com.nubank.authorizer.Account
import org.scalatest.EitherValues
import org.scalatest.wordspec.AnyWordSpec

class InMemoryAccountRepositorySpec extends AnyWordSpec with EitherValues {
  "The in-memory account repository" when {
    "asked to save an account" should {
      "add to the in memory variable" in {
        val repo = new InMemoryAccountRepository()
        repo.save(Account.create(true, 100))

        assert(repo.get.isDefined)
      }
      "return an error if the account already exists" in {
        val repo = new InMemoryAccountRepository()
        repo.save(Account.create(true, 100))

        assert(repo.save(Account.create(true, 200)).isLeft)
      }
    }
    "asked to update an account" should {
      "change the stored value" in {
        val repo = new InMemoryAccountRepository()
        repo.save(Account.create(true, 100))
        repo.update(Account.create(true, 50))

        assert(repo.get.get.availableLimit == 50)
      }
      "return an error when there is no account" in {
        val repo = new InMemoryAccountRepository()

        assert(repo.update(Account.create(true, 50)).isLeft)
      }
    }
  }
}
