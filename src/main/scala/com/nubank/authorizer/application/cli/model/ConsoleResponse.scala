package com.nubank.authorizer.application.cli.model

import com.nubank.authorizer.domain.model.Authorization
import io.circe.generic.extras.ConfiguredJsonCodec
import com.nubank.authorizer.application.cli.model.json._

@ConfiguredJsonCodec case class ConsoleResponse(account: ConsoleAccount, violations: List[String])

object ConsoleResponse {
  def from(auth: Authorization): ConsoleResponse = {
    ConsoleResponse(
      account = ConsoleAccount.from(auth.account),
      violations = auth.violations.map(_.toString)
    )
  }
}
