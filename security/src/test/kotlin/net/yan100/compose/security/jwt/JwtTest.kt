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
package net.yan100.compose.security.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import net.yan100.compose.core.util.encrypt.Keys
import net.yan100.compose.security.jwt.consts.IssuerParam
import net.yan100.compose.security.jwt.consts.VerifierParam
import org.junit.jupiter.api.Test

class JwtTest {

  @Test
  fun testIssuerAndVerifier() {
    val mapper = ObjectMapper()
    val eccPair = Keys.generateEccKeyPair()!!
    val rsaPair = Keys.generateRsaKeyPair()!!

    val issuer =
      JwtIssuer.createIssuer()
        .issuer("t")
        .id("1")
        .signatureIssuerKey(rsaPair.rsaPrivateKey!!)
        .signatureVerifyKey(rsaPair.rsaPublicKey!!)
        .contentEncryptKey(eccPair.eccPublicKey!!)
        .serializer(mapper)
        .build()

    val verifier =
      JwtVerifier.createVerifier()
        .issuer("t")
        .id("1")
        .contentDecryptKey(eccPair.eccPrivateKey!!)
        .signatureVerifyKey(rsaPair.rsaPublicKey!!)
        .serializer(mapper)
        .build()

    val inputs = IssuerParam<Any, Any>(signatureKey = rsaPair.rsaPrivateKey)
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
