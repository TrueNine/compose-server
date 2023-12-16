package net.yan100.compose.security.autoconfig


import com.fasterxml.jackson.databind.ObjectMapper
import net.yan100.compose.core.encrypt.Keys
import net.yan100.compose.core.encrypt.PemFormat
import net.yan100.compose.core.lang.pnt
import net.yan100.compose.security.jwt.JwtIssuer
import net.yan100.compose.security.jwt.JwtVerifier
import net.yan100.compose.security.jwt.consts.IssuerParam
import net.yan100.compose.security.jwt.consts.VerifierParam
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.temporal.ChronoUnit
import kotlin.test.assertNotNull


class FileKeyRepoTest {

  @Test
  fun findKeyByName() {
    val e = Keys.generateEccKeyPair()

    Keys.writeKeyToPem(e!!.eccPrivateKey!!).pnt()
    "=========".pnt()
    Keys.writeKeyToPem(e.eccPublicKey!!).pnt()

    val f = FileKeyRepo()
    val b = f.findEccPrivateKeyByName("acv.pem")
    val c = f.findEccPublicKeyByName("acc.pem")

    val eccPair = f.findEccKeyPairByName("acc.pem", "acv.pem")

    b.pnt()
    c.pnt()
    eccPair.pnt()
  }

  @Test
  fun findRsa() {
    val r = Keys.generateRsaKeyPair()!!
    PemFormat.ofKey(r.rsaPublicKey!!).pnt()
    PemFormat.ofKey(r.rsaPrivateKey!!).pnt()

    val f = FileKeyRepo()
    val k = f.findRsaKeyPairByName("rcc.pem", "rcv.pem")!!
    k.rsaPublicKey.pnt()
    k.rsaPrivateKey.pnt()
  }

  @Test
  fun testReadJwt() {
    val f = FileKeyRepo()
    val e = f.jwtEncryptDataIssuerEccKeyPair()
    val s = f.jwtSignatureIssuerRsaKeyPair()
    assertNotNull(e)
    assertNotNull(e.eccPublicKey)
    assertNotNull(e.eccPrivateKey)
    assertNotNull(s)
    assertNotNull(s.rsaPublicKey)
    assertNotNull(s.rsaPrivateKey)

    val iss = JwtIssuer.createIssuer()
      .serializer(ObjectMapper())
      .expireFromDuration(Duration.of(100, ChronoUnit.SECONDS))
      .signatureIssuerKey(s.rsaPrivateKey!!)
      .signatureVerifyKey(s.rsaPublicKey!!)
      .contentEncryptKey(e.eccPublicKey!!)
      .contentDecryptKey(e.eccPrivateKey!!)
      .build()

    val ver = JwtVerifier.createVerifier()
      .serializer(ObjectMapper())
      .contentDecryptKey(e.eccPrivateKey!!)
      .signatureVerifyKey(s.rsaPublicKey!!)
      .build()

    val issToken = iss.issued(IssuerParam<Any, Any>().apply {
      encryptedDataObj = "1" to "2"
      subjectObj = "3" to "4"
    })
    val res = ver.verify(VerifierParam<Any, Any>(issToken,subjectTargetType = Any::class.java, encryptDataTargetType = Any::class.java))
    res.subject .pnt()
    res.decryptedData.pnt()
  }
}
