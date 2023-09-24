package net.yan100.compose.security.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import net.yan100.compose.core.encrypt.Keys
import net.yan100.compose.security.jwt.consts.IssuerParamModel
import net.yan100.compose.security.jwt.consts.VerifierParamModel
import org.testng.annotations.Test

class JwtTest {

  @Test
  fun testIssuerAndVerifier() {
    val mapper = ObjectMapper()
    val eccPair = Keys.generateEccKeyPair()!!
    val rsaPair = Keys.generateRsaKeyPair()!!
    val issuer = JwtIssuer.createIssuer()
      .issuer("t")
      .id("1")
      .signatureIssuerKey(rsaPair.rsaPrivateKey!!)
      .signatureVerifyKey(rsaPair.rsaPublicKey!!)
      .contentEncryptKey(eccPair.eccPublicKey!!)
      .serializer(mapper)
      .build()

    val verifier = JwtVerifier.createVerifier()
      .issuer("t")
      .id("1")
      .contentDecryptKey(eccPair.eccPrivateKey!!)
      .signatureVerifyKey(rsaPair.rsaPublicKey!!)
      .serializer(mapper)
      .build()

    val inputs = IssuerParamModel<Any, Any>(signatureKey = rsaPair.rsaPrivateKey)
    inputs.encryptedDataObj = "我日了狗"
    inputs.subjectObj = mutableListOf("123", "444")
    inputs.encryptedDataObj = mutableListOf("123", "444")

    val token = issuer.issued(inputs)
    val outputs = VerifierParamModel(
      token = token,
      subjectTargetType = Any::class.java,
      encryptDataTargetType = Any::class.java
    )
    val parsed = verifier.verify(outputs)
    println(token)
    println(parsed.decryptedData)
    println(parsed.subject)
  }
}
