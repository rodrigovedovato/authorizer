package com.nubank.authorizer

import org.scalatest.EitherValues
import org.scalatest.wordspec.AnyWordSpec

import java.time.OffsetDateTime

class WindowSpec extends AnyWordSpec with EitherValues {
  val now: OffsetDateTime = OffsetDateTime.now()

  "A window" when {
    val window = Window(interval = 2, size = 3)

    "initialized" should {
      "have size 0" in {
        assert(window.count == 0)
      }
      "have size 1" in {
        assertResult(1) {
          window.add(Transaction(merchant = "The Blue Pub", amount = 20, time = now)).value.count
        }
      }
    }

    "fed with a transaction" should {
      "clear itself" in {
        val result = for {
          w1 <- window.add(Transaction(merchant = "The Blue Pub", amount = 20, time = now))
          w2 <- w1.add(Transaction(merchant = "O'Malleys Pub", amount = 100, time = now.plusMinutes(3)))
        } yield w2

        assertResult(1)(result.value.count)
      }

      "overflow" in {
        val result = for {
          w1 <- window.add(Transaction(merchant = "The Blue Pub", amount = 20, time = now))
          w2 <- w1.add(Transaction(merchant = "O'Malleys Pub", amount = 100, time = now.plusSeconds(30)))
          w3 <- w2.add(Transaction(merchant = "Deep Bar 611", amount = 100, time = now.plusSeconds(60)))
          w4 <- w3.add(Transaction(merchant = "Republic Pub", amount = 100, time = now.plusSeconds(75)))
        } yield w4

        assertResult(Window.errors.WindowOverflow)(result.left.value)
      }

      "not overflow" in {
        val result = for {
          w1 <- window.add(Transaction(merchant = "The Blue Pub", amount = 20, time = now))
          w2 <- w1.add(Transaction(merchant = "O'Malleys Pub", amount = 100, time = now.plusSeconds(30)))
          w3 <- w2.add(Transaction(merchant = "Deep Bar 611", amount = 100, time = now.plusSeconds(60)))
          w4 <- w3.add(Transaction(merchant = "Republic Pub", amount = 100, time = now.plusMinutes(3))) // More than 2 minutes apart
        } yield w4

        assertResult(Window.errors.WindowOverflow)(result.left.value)
      }

      "warn this is a duplicate transaction" in {
        val result = for {
          w1 <- window.add(Transaction(merchant = "The Blue Pub", amount = 20, time = now))
          w2 <- w1.add(Transaction(merchant = "O'Malleys Pub", amount = 100, time = now.plusSeconds(30)))
          w3 <- w2.add(Transaction(merchant = "O'Malleys Pub", amount = 100, time = now.plusSeconds(60)))
        } yield w3

        assertResult(Window.errors.DuplicateEntry)(result.left.value)
      }

      "not warn this is a duplicate transaction" in {
        val result = for {
          w1 <- window.add(Transaction(merchant = "The Blue Pub", amount = 20, time = now))
          w2 <- w1.add(Transaction(merchant = "O'Malleys Pub", amount = 100, time = now.plusSeconds(30)))
          w3 <- w2.add(Transaction(merchant = "O'Malleys Pub", amount = 100, time = now.plusMinutes(3))) // More than 2 minutes apart
        } yield w3

        assertResult(Window.errors.DuplicateEntry)(result.left.value)
      }
    }
  }
}
