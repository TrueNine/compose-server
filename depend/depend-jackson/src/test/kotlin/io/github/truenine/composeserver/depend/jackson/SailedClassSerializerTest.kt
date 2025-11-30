package io.github.truenine.composeserver.depend.jackson

import kotlin.test.Test
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Assertions.assertNotNull
import org.springframework.boot.test.context.SpringBootTest
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.exc.InvalidDefinitionException

@SpringBootTest
class SailedClassSerializerTest {
  val defaultMapper: ObjectMapper = ObjectMapper()

  sealed class IpAddress {
    data class V4(val v1: Long, val v2: Long, val v3: Long, val v4: Long) : IpAddress()

    data class V6(val ip: String) : IpAddress()
  }

  @Test
  fun `jackson cannot serialize sealed class by default`() {
    val ipv4 = IpAddress.V4(127, 0, 0, 1)
    val ipV4 = defaultMapper.writeValueAsString(ipv4)
    assertNotNull(ipV4)
    assertFailsWith<InvalidDefinitionException> { defaultMapper.readValue(ipV4, IpAddress::class.java) }
  }
}
