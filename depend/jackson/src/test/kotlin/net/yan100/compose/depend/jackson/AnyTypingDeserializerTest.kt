package net.yan100.compose.depend.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.Resource
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import net.yan100.compose.testtoolkit.log
import net.yan100.compose.typing.HttpStatusTyping
import net.yan100.compose.typing.UserAgents
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class AnyTypingDeserializerTest {
  lateinit var mapper: ObjectMapper
    @Resource set

  @Test
  fun `确保 撤销了枚举类型后，测试逻辑仍然可以运行`() {
    val stringTyping = UserAgents.CHROME_WIN_103
    val intTyping = HttpStatusTyping._403
    val dd = AnyTypingRecord(stringTyping, intTyping)
    val json = mapper.writeValueAsString(dd)
    log.info("json: {}", json)
    assertFailsWith<AssertionError> {
      assertEquals("""{"stringTyping1":"${UserAgents.CHROME_WIN_103.value}","intTyping2":${HttpStatusTyping._403.value}}""", json)
    }
    assertEquals("""{"stringTyping1":"CHROME_WIN_103","intTyping2":"_403"}""", json)
    val des = mapper.readValue(json, AnyTypingRecord::class.java)
    log.info("des: {}", des)
    assertNotNull(des)
  }
}
