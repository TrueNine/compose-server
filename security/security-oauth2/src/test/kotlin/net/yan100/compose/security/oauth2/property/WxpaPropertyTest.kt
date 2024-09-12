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
package net.yan100.compose.security.oauth2.property

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.yan100.compose.core.encrypt.sha1
import net.yan100.compose.security.oauth2.SecurityOauth2TestEntrance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest(classes = [SecurityOauth2TestEntrance::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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

  @Test
  fun `test signature`() {
    val j = "O3SMpm8bG7kJnF36aXbe82tYKil59B0Wt4bWcCHEXGNLaUh3dx5I1dqW_Zzo2uM8-eOYUV6TD3-cDTTyF_IqPQ"
    val n = "jLFUE4XXOwx11VlfQm535xqU5k1R6g2g"
    val t = "1711251297"
    val u = "https://frp.yifajucai.com/wxpa/auth/register/sharecode?shareCode=202403240215504199616"
    val result = "0096f0f65249ea6059784e51761680aa7d8d8db7"
    val str =
      "jsapi_ticket=O3SMpm8bG7kJnF36aXbe82tYKil59B0Wt4bWcCHEXGNLaUh3dx5I1dqW_Zzo2uM8-eOYUV6TD3-cDTTyF_IqPQ&noncestr=jLFUE4XXOwx11VlfQm535xqU5k1R6g2g&timestamp=1711251297&url=https://frp.yifajucai.com/wxpa/auth/register/sharecode?shareCode=202403240215504199616"

    runBlocking {
      val splitUrl = u.split("#")[0]

      val b = mutableMapOf("noncestr" to n, "jsapi_ticket" to j, "timestamp" to t, "url" to splitUrl).map { "${it.key}=${it.value}" }.sorted().joinToString("&")

      assertEquals(b, str)
      assertEquals(result, b.sha1)
    }
  }
}
