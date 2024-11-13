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
package net.yan100.compose.security

import com.fasterxml.jackson.databind.ObjectMapper
import net.yan100.compose.security.crypto.FileKeyRepo
import net.yan100.compose.security.crypto.Keys
import net.yan100.compose.security.crypto.PemFormat
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

    println(Keys.writeKeyToPem(e!!.privateKey))
    println("=========")
    println(Keys.writeKeyToPem(e.publicKey))

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
    println(PemFormat[r.publicKey])
    println(PemFormat[r.privateKey])

    val f = FileKeyRepo()
    val k = f.findRsaKeyPairByName("rcc.pem", "rcv.pem")!!
    println(k.publicKey)
    println(k.privateKey)
  }

  @Test
  fun testReadJwt() {
    val f = FileKeyRepo()
    val e = f.jwtEncryptDataIssuerEccKeyPair()
    val s = f.jwtSignatureIssuerRsaKeyPair()
    assertNotNull(e)
    assertNotNull(e.publicKey)
    assertNotNull(e.privateKey)
    assertNotNull(s)
    assertNotNull(s.publicKey)
    assertNotNull(s.privateKey)

    val iss =
      JwtIssuer.createIssuer()
        .serializer(ObjectMapper())
        .expireFromDuration(Duration.of(100, ChronoUnit.SECONDS))
        .signatureIssuerKey(s.privateKey)
        .signatureVerifyKey(s.publicKey)
        .contentEncryptKey(e.publicKey)
        .contentDecryptKey(e.privateKey)
        .build()

    val ver = JwtVerifier.createVerifier().serializer(ObjectMapper()).contentDecryptKey(e.privateKey).signatureVerifyKey(s.publicKey).build()

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
