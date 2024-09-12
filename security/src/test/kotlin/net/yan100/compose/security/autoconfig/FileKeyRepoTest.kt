/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.security.autoconfig

import com.fasterxml.jackson.databind.ObjectMapper
import net.yan100.compose.core.encrypt.Keys
import net.yan100.compose.core.encrypt.PemFormat
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

    println(Keys.writeKeyToPem(e!!.eccPrivateKey!!))
    println("=========")
    println(Keys.writeKeyToPem(e.eccPublicKey!!))

    val f = FileKeyRepo()
    val b = f.findEccPrivateKeyByName("acv.pem")
    val c = f.findEccPublicKeyByName("acc.pem")

    val eccPair = f.findEccKeyPairByName("acc.pem", "acv.pem")

    println(b)
    println(c)
    println(eccPair)
  }

  @Test
  fun findRsa() {
    val r = Keys.generateRsaKeyPair()!!
    println(PemFormat[r.rsaPublicKey!!])
    println(PemFormat[r.rsaPrivateKey!!])

    val f = FileKeyRepo()
    val k = f.findRsaKeyPairByName("rcc.pem", "rcv.pem")!!
    println(k.rsaPublicKey)
    println(k.rsaPrivateKey)
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

    val iss =
      JwtIssuer.createIssuer()
        .serializer(ObjectMapper())
        .expireFromDuration(Duration.of(100, ChronoUnit.SECONDS))
        .signatureIssuerKey(s.rsaPrivateKey!!)
        .signatureVerifyKey(s.rsaPublicKey!!)
        .contentEncryptKey(e.eccPublicKey!!)
        .contentDecryptKey(e.eccPrivateKey!!)
        .build()

    val ver = JwtVerifier.createVerifier().serializer(ObjectMapper()).contentDecryptKey(e.eccPrivateKey!!).signatureVerifyKey(s.rsaPublicKey!!).build()

    val issToken =
      iss.issued(
        IssuerParam<Any, Any>().apply {
          encryptedDataObj = "1" to "2"
          subjectObj = "3" to "4"
        }
      )
    val res = ver.verify(VerifierParam(issToken, subjectTargetType = Any::class.java, encryptDataTargetType = Any::class.java))
    println(res?.subject)
    println(res?.decryptedData)
  }
}
