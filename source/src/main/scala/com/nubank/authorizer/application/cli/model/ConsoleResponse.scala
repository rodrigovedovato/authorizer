package com.nubank.authorizer.application.cli.model

import com.nubank.authorizer.domain.model.Authorization
import io.circe.generic.extras.ConfiguredJsonCodec
import com.nubank.authorizer.application.cli.model.json._
import com.nubank.authorizer.domain.model.Authorization.Violation

case class ConsoleResponse(account: Option[ConsoleAccount], violations: List[String])

object ConsoleResponse {
  val violationToString: Violation => String = {
    case Authorization.CardNotActive => "card-not-active"
    case Authorization.AccountNotInitialized => "account-not-initialized"
    case Authorization.DoubleTransaction => "double-transaction"
    case Authorization.InsufficientLimit => "insufficient-limit"
    case Authorization.AccountAlreadyInitialized => "account-already-initialized"
    case Authorization.HighFrequencySmallInterval => "high-frequency-small-interval"
  }

  def from(auth: Authorization): ConsoleResponse = {
    ConsoleResponse(
      account = Option(auth.account).map(ConsoleAccount.from),
      violations = auth.violations.map(violationToString)
    )
  }
}
