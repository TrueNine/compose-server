/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
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
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.core.encrypt.base64

import org.junit.jupiter.api.Test
import kotlin.test.assertNotEquals

class Base64HelperTest {

  @Test
  fun testEncode() {
    val t = "这是一段测试字符串"
    val r = net.yan100.compose.core.encrypt.Base64Helper.encode(t.toByteArray())
    assertNotEquals(t, r, "没有进行编码")
  }

  @Test
  fun testEncodeToByte() {
    val t = "这是一段测试字符串".toByteArray()
    val r = net.yan100.compose.core.encrypt.Base64Helper.encodeToByte(t)
    assertNotEquals(t, r, "没有进行编码")
  }
}
