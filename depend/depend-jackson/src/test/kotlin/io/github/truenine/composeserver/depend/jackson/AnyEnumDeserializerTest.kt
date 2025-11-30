package io.github.truenine.composeserver.depend.jackson

import io.github.truenine.composeserver.enums.HttpStatus
import io.github.truenine.composeserver.enums.UserAgents
import io.github.truenine.composeserver.testtoolkit.log
import jakarta.annotation.Resource
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import tools.jackson.databind.ObjectMapper
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest
class AnyEnumDeserializerTest {
  lateinit var mapper: ObjectMapper
    @Resource set

  @Test
  fun `ensure tests still run after removing enum types`() {
    val stringTyping = UserAgents.CHROME_WIN_103
    val intTyping = HttpStatus._403
    val dd = AnyEnumRecord(stringTyping, intTyping)
    val json = mapper.writeValueAsString(dd)
    log.info("json: {}", json)
    // Test that the JSON contains the actual values, not the enum names
    assertEquals("""{"stringTyping1":"${UserAgents.CHROME_WIN_103.value}","intTyping2":${HttpStatus._403.value}}""", json)
    val des = mapper.readValue(json, AnyEnumRecord::class.java)
    log.info("des: {}", des)
    assertNotNull(des)
  }
}
