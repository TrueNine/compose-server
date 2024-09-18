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
package net.yan100.compose.security.crpyot


import net.yan100.compose.core.hasText
import net.yan100.compose.security.crypto.Keys
import net.yan100.compose.testtookit.log
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class KeysTest {
  @Test
  fun `write key to pem`() {
    val a = Keys.writeKeyToPem(Keys.generateRsaKeyPair()!!.privateKey)
    val b = Keys.writeKeyToPem(Keys.generateEccKeyPair()!!.privateKey)

    log.info(a)
    log.info(b)

    assertTrue(a.hasText())
    assertTrue(b.hasText())
    assertTrue(a.contains("-----END RSA PKCS#8-----"))
    assertTrue(b.contains("-----END EC PKCS#8-----"))
  }
}
