package net.yan100.compose.depend.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.Resource
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import net.yan100.compose.domain.IPageParam
import net.yan100.compose.domain.IPageParamLike
import net.yan100.compose.testtookit.log
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class IPageParamTest {
  lateinit var mapper: ObjectMapper
    @Resource set

  @Test
  fun `success fill get method request body policy`() {
    val pq = mapper.readValue("""{"o":1,"s":41}""", IPageParam::class.java)
    assertNotNull(pq)
    assertEquals(1, pq.o)
    assertEquals(41, pq.s)
    val pq2 = mapper.writeValueAsString(pq)
    log.info("pq2: {}", pq2)
    assertEquals("""{"o":1,"s":41}""", pq2)
    pq2?.also {
      val des = mapper.readValue(it, IPageParamLike::class.java)
      assertEquals(pq, des)
    }
  }
}
