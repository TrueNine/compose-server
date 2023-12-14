package net.yan100.compose.security.extensionfunctions

import net.yan100.compose.security.UserDetailsWrapper
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

fun AuthenticationManager.authenticateByAccount(account: String, password: String, unauthorized: () -> Unit): UserDetailsWrapper? {
  val principal = this.authenticate(UsernamePasswordAuthenticationToken(account, password)).principal as? UserDetailsWrapper
  if (null == principal) {
    unauthorized()
    return null
  }
  else return principal
}
