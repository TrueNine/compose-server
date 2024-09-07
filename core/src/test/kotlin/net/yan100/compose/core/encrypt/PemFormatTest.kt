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
import net.yan100.compose.core.models.PemFormat
import org.junit.jupiter.api.Test
import kotlin.test.DefaultAsserter.assertEquals

class PemFormatTest {

  @Test
  fun `test PemFormat`() {
    val key = "-----BEGIN RSA PRIVATE KEY-----\nMIICXQIBAAKBgQDo56Mh0tFwuxa8ZSP1L1M0eWtU+1G+1tWp2rJ4p8k8R83a ...\n-----END RSA PRIVATE KEY-----"
    val pemFormat = PemFormat(key)

    assertEquals("未识别出参数", "RSA PRIVATE KEY", pemFormat.schema)
  }

  @Test
  fun `test PemFormat with invalid key`() {
    val key = "-----BEGIN CERTIFICATE-----\nMIICXQIBAAKBgQDo56Mh0tFwuxa8ZSP1L1M0eWtU+1G+1tWp2rJ4p8k8R83a ...\n-----END CERTIFICATE-----"
    PemFormat(key)
  }

  @Test
  fun testParseBase64() {
    val base64 = "base64string1241251251252362374575668679679679vvwsetgwetweywy262362737fsdfsehrejertjrtyj"
    val keyType = "rsa private key"
    val result = PemFormat[base64, keyType]
    val pem = PemFormat(result)
    // Call the function to be tested and check the result
    println(result)
  }
}
