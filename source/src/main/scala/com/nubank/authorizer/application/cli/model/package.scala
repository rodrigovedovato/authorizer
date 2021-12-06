package com.nubank.authorizer.application.cli

import com.nubank.authorizer.application.cli.model.requests.{CreateAccountRequest, ProcessTransactionRequest, Request}
import io.circe.{Decoder, Encoder, Json}
import io.circe.generic.extras.Configuration
import cats.syntax.functor._
import io.circe.generic.semiauto._

package object model {
  object json {
    implicit val config: Configuration = Configuration.default.withKebabCaseMemberNames.withDiscriminator("type")

    implicit val decodeRequest: Decoder[Request] = List[Decoder[Request]](
      Decoder[CreateAccountRequest].widen, Decoder[ProcessTransactionRequest].widen
    ).reduceLeft(_ or _)

    def nullToEmptyObj(v: Json): Json = if (v.isNull) Json.obj() else v

    implicit val decodeConsoleResponse: Encoder[ConsoleResponse] = deriveEncoder[ConsoleResponse].mapJsonObject { obj =>
      obj.mapValues { json =>
        nullToEmptyObj(json)
      }
    }
  }
}
