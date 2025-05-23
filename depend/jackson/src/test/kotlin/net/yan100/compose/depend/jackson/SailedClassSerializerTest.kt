package net.yan100.compose.depend.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException
import net.yan100.compose.i16
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

@SpringBootTest
class SailedClassSerializerTest {
  val defaultMapper: ObjectMapper = ObjectMapper()

  sealed class IpAddress {
    data class V4(val v1: i16, val v2: i16, val v3: i16, val v4: i16) :
      IpAddress()

    data class V6(val ip: String) : IpAddress()
  }

  @Test
  fun `jackson 默认情况下不能序列化密封类`() {
    val ipv4 = IpAddress.V4(127, 0, 0, 1)
    val ipV4 = defaultMapper.writeValueAsString(ipv4)
    assertNotNull(ipV4)
    assertFailsWith<InvalidDefinitionException> {
      defaultMapper.readValue(ipV4, IpAddress::class.java)
    }
  }
}
