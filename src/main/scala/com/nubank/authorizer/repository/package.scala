package com.nubank.authorizer

package object repository {
  case object AccountNotInitialized
  case class AccountAlreadyInitialized(currentAccount: Account)
}
