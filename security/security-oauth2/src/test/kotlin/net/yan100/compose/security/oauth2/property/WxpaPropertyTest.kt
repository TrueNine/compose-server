package net.yan100.compose.security.oauth2.property

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.yan100.compose.security.oauth2.Oauth2TestEntrance
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertNotNull

@SpringBootTest(classes = [Oauth2TestEntrance::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WxpaPropertyTest {
    @Autowired
    lateinit var w: WxpaProperty

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
