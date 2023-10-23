package net.yan100.compose.core.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import net.yan100.compose.core.CoreEntrance
import net.yan100.compose.core.lang.toDate
import net.yan100.compose.core.lang.toLocalDatetime
import net.yan100.compose.core.lang.toLong
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals

@SpringBootTest(classes = [CoreEntrance::class])
class JsonSerializerTest {

  @Autowired
  lateinit var mapper: ObjectMapper

  @Test
  fun testLocalDatetime() {
    val localDatetime = Date().toLocalDatetime()
    val json = mapper.writeValueAsString(localDatetime)
    println(json)
    val local = mapper.readValue(json, LocalDateTime::class.java)
    println(local.toDate().toLong())
    assertEquals(local.toDate().toLong(), localDatetime.toDate().toLong())
  }
}
