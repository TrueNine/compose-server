package com.truenine.component.security.jwt

import com.truenine.component.core.encrypt.Keys
import com.truenine.component.core.encrypt.base64.Base64Helper
import org.testng.annotations.Test

class JwtTest {
  @Test
  fun testIssuerAndVerifier() {
    val eccPair = Keys.generateEccKeyPair()!!
    val rsaPair = Keys.generateRsaKeyPair()!!
    val issuer = JwtIssuer.createIssuer()
      .issuer("t")
      .id("1")
      .serializer()
      .build()

    val verifier = JwtVerifier.createVerifier()
      .issuer("t")
      .id("1")
      .serializer()
      .build()

    val inputs = IssuerParams<Any, Any>(signatureKey = rsaPair.rsaPrivateKey)
    inputs.encryptedDataObj = "我日了狗"
    inputs.subjectObj = mutableListOf("123", "444")

    val token = issuer.issued(inputs)
    val outputs = VerifierParams<Any, Any>(
      token = token,
      subjectTargetType = Any::class,
      signatureKey = rsaPair.rsaPublicKey
    )
    val parsed = verifier.verify(outputs)
    println(token)
    println(parsed!!.encryptedData)
    println(parsed.subject)
  }
}
