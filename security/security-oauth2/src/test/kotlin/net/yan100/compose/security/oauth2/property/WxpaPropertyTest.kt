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
package net.yan100.compose.security.oauth2.property

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.yan100.compose.security.oauth2.Oauth2TestEntrance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
  classes = [Oauth2TestEntrance::class],
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class WxpaPropertyTest {
  @Autowired lateinit var w: WxpaProperty

  /** 如果测试失败，请暂时关闭梯子， 如果还是不行，请检查你的 DNS 配置 */
  @Test
  fun `test get access token`() {
    runBlocking {
      delay(4000)
      val a = w.accessToken
      val b = w.jsapiTicket
      assertNotNull(a)
      assertNotNull(b)
      println(a)
      println(b)
    }
  }
}
