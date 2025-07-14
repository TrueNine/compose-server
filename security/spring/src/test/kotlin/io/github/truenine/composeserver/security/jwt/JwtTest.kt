package io.github.truenine.composeserver.security.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.truenine.composeserver.security.crypto.CryptographicKeyManager
import io.github.truenine.composeserver.security.jwt.consts.IssuerParam
import io.github.truenine.composeserver.security.jwt.consts.VerifierParam
import org.junit.jupiter.api.Test

class JwtTest {

  @Test
  fun testIssuerAndVerifier() {
    val mapper = ObjectMapper()
    val eccPair = CryptographicKeyManager.generateEccKeyPair(CryptographicKeyManager.generateRandomAsciiString())!!
    val rsaPair = CryptographicKeyManager.generateRsaKeyPair()!!

    val issuer =
      JwtIssuer.createIssuer()
        .issuer("t")
        .id("1")
        .signatureIssuerKey(rsaPair.privateKey)
        .signatureVerifyKey(rsaPair.publicKey)
        .contentEncryptKey(eccPair.publicKey)
        .serializer(mapper)
        .build()

    val verifier =
      JwtVerifier.createVerifier().issuer("t").id("1").contentDecryptKey(eccPair.privateKey).signatureVerifyKey(rsaPair.publicKey).serializer(mapper).build()

    val inputs = IssuerParam<Any, Any>(signatureKey = rsaPair.privateKey)
    inputs.encryptedDataObj = "我日了狗"
    inputs.subjectObj = mutableListOf("123", "444")
    inputs.encryptedDataObj = mutableListOf("123", "444")

    val token = issuer.issued(inputs)
    val outputs = VerifierParam(token = token, subjectTargetType = Any::class.java, encryptDataTargetType = Any::class.java)
    val parsed = verifier.verify(outputs)
    println(token)
    println(parsed?.decryptedData)
    println(parsed?.subject)
  }
}
