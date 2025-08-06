package io.github.truenine.composeserver.security

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication

/** Test AuthenticationManager extension functions */
class AuthenticationManagerFnsTest {

  @Test
  fun `test authenticateByAccount with valid credentials`() {
    val authenticationManager = mockk<AuthenticationManager>()
    val authentication = mockk<Authentication>()
    val userDetailsWrapper = mockk<UserDetailsWrapper>()

    val account = "testuser"
    val password = "testpass"
    var unauthorizedCalled = false

    every { authentication.principal } returns userDetailsWrapper
    every { authenticationManager.authenticate(any<UsernamePasswordAuthenticationToken>()) } returns authentication

    val result = authenticationManager.authenticateByAccount(account, password) { unauthorizedCalled = true }

    assertEquals(userDetailsWrapper, result)
    assertEquals(false, unauthorizedCalled)
    verify { authenticationManager.authenticate(any<UsernamePasswordAuthenticationToken>()) }
  }

  @Test
  fun `test authenticateByAccount with null principal`() {
    val authenticationManager = mockk<AuthenticationManager>()
    val authentication = mockk<Authentication>()

    val account = "testuser"
    val password = "testpass"
    var unauthorizedCalled = false

    every { authentication.principal } returns null
    every { authenticationManager.authenticate(any<UsernamePasswordAuthenticationToken>()) } returns authentication

    val result = authenticationManager.authenticateByAccount(account, password) { unauthorizedCalled = true }

    assertNull(result)
    assertEquals(true, unauthorizedCalled)
    verify { authenticationManager.authenticate(any<UsernamePasswordAuthenticationToken>()) }
  }

  @Test
  fun `test authenticateByAccount with wrong principal type`() {
    val authenticationManager = mockk<AuthenticationManager>()
    val authentication = mockk<Authentication>()

    val account = "testuser"
    val password = "testpass"
    val wrongPrincipal = "string-principal"
    var unauthorizedCalled = false

    every { authentication.principal } returns wrongPrincipal
    every { authenticationManager.authenticate(any<UsernamePasswordAuthenticationToken>()) } returns authentication

    val result = authenticationManager.authenticateByAccount(account, password) { unauthorizedCalled = true }

    assertNull(result)
    assertEquals(true, unauthorizedCalled)
    verify { authenticationManager.authenticate(any<UsernamePasswordAuthenticationToken>()) }
  }
}
