package net.yan100.compose.security.oauth2.property

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlinx.coroutines.runBlocking
import net.yan100.compose.security.crypto.sha1
import net.yan100.compose.testtoolkit.log
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext

@SpringBootTest
class WxpaPropertyTest {
  lateinit var wxpa: WxpaProperty
  lateinit var ctx: ApplicationContext

  lateinit var alwaysWxpa: WxpaProperty

  @BeforeTest
  fun setup() {
    MockKAnnotations.init(this)
    ctx = mockk(relaxed = true)
    wxpa = mockk()

    log.info("init WxpaProperty")

    // 初始化返回对象
    alwaysWxpa =
      WxpaProperty().apply {
        preValidToken = "DPlKMdgQG6jvOXbISk1vK2FdpKxd2Ip6"
        appId = "wx3d24564f9a85044d"
        appSecret = "d1ea9bdbd65b602679db8575dbc8461f"
        accessToken =
          "84_fap0UeIti9S48uvh49cKAVzoCbHiJy-HGetAvvmWniYOas6ZnFg4_llyTrQGzj6x-co-AxbNsfS4Pm3Ud3iAO-Gnom7o29ddYb8GQFSalS56sTWfVoexOfBkpqoYTJeAJANIY"
        jsapiTicket =
          "O3SMpm8bG7kJnF36aXbe82tYKil59B0Wt4bWcCHEXGP7GczyBmy4BGY03XAkcycQSqzDrpW0mk8tZEs1I5Ueiw"
      }
    every { ctx.getBean(WxpaProperty::class.java) } returns alwaysWxpa
    wxpa = ctx.getBean(WxpaProperty::class.java)
  }

  @Test
  fun `test get access token`() {

    // 确保使用原始对象
    assertEquals(alwaysWxpa, ctx.getBean(WxpaProperty::class.java))

    val a = wxpa.preValidToken
    val b = wxpa.appId
    val c = wxpa.appSecret
    val d = wxpa.accessToken
    val e = wxpa.jsapiTicket

    log.info(a)
    log.info(b)
    log.info(c)
    log.info(d)
    log.info(e)

    assertNotNull(a)
    assertNotNull(b)
    assertNotNull(c)
    assertNotNull(d)
    assertNotNull(e)
  }

  @Test
  fun `test signature`() {
    val j =
      "O3SMpm8bG7kJnF36aXbe82tYKil59B0Wt4bWcCHEXGNLaUh3dx5I1dqW_Zzo2uM8-eOYUV6TD3-cDTTyF_IqPQ"
    val n = "jLFUE4XXOwx11VlfQm535xqU5k1R6g2g"
    val t = "1711251297"
    val u =
      "https://frp.yifajucai.com/wxpa/auth/register/sharecode?shareCode=202403240215504199616"
    val result = "0096f0f65249ea6059784e51761680aa7d8d8db7"
    val str =
      "jsapi_ticket=O3SMpm8bG7kJnF36aXbe82tYKil59B0Wt4bWcCHEXGNLaUh3dx5I1dqW_Zzo2uM8-eOYUV6TD3-cDTTyF_IqPQ&noncestr=jLFUE4XXOwx11VlfQm535xqU5k1R6g2g&timestamp=1711251297&url=https://frp.yifajucai.com/wxpa/auth/register/sharecode?shareCode=202403240215504199616"

    runBlocking {
      val splitUrl = u.split("#")[0]

      val b =
        mutableMapOf(
          "noncestr" to n,
          "jsapi_ticket" to j,
          "timestamp" to t,
          "url" to splitUrl,
        )
          .map { "${it.key}=${it.value}" }
          .sorted()
          .joinToString("&")

      assertEquals(b, str)
      assertEquals(result, b.sha1)
    }
  }
}
