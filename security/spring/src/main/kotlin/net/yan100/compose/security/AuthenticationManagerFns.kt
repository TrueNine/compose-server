package net.yan100.compose.security

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

fun AuthenticationManager.authenticateByAccount(
  account: String,
  password: String,
  unauthorized: () -> Unit,
): UserDetailsWrapper? {
  val principal =
    this.authenticate(UsernamePasswordAuthenticationToken(account, password))
      .principal as? UserDetailsWrapper
  return if (null == principal) {
    unauthorized()
    null
  } else principal
}
