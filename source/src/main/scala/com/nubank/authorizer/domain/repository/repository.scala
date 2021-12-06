package com.nubank.authorizer.domain

import com.nubank.authorizer.domain.model.Account

package object repository {
  case object AccountNotFound
  case class AccountConflict(currentAccount: Account)
}
