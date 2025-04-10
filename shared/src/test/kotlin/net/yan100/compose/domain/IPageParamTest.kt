package net.yan100.compose.domain

import jakarta.annotation.Resource
import net.yan100.compose.Pq
import net.yan100.compose.bool
import net.yan100.compose.i32
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@AutoConfigureMockMvc
@SpringBootTest
class IPageParamTest {
  lateinit var controller: TestPqController
    @Resource set

  lateinit var mvc: MockMvc
    @Resource set

  @BeforeTest
  fun setup() {
    assertNotNull(controller)
    assertNotNull(mvc)
  }

  @Test
  fun `success fill get method default policy`() {
    mvc.get("/v1/pq/get/default?o=1&s=24").andExpect {
      content {
        jsonPath("$.o") {
          exists()
          isNumber()
          value(1)
        }
        jsonPath("$.s") {
          exists()
          isNumber()
          value(24)
        }
      }
      status { isOk() }
    }
  }

  @Test
  fun `success get from like empty`() {
    val e =
      Pq[
        object : IPageParamLike {
          override val s: i32?
            get() = null

          override val o: i32?
            get() = null

          @Deprecated("禁用分页是不明智的选择", level = DeprecationLevel.ERROR)
          override val u: bool?
            get() = null
        }]
    assertEquals(Pq.MIN_OFFSET, e.o)
    assertEquals(Pq.MAX_PAGE_SIZE, e.s)
  }

  @Test
  fun `fail create pq`() {
    val pq = Pq[-1, -13]
    assertEquals(Pq.MIN_OFFSET, pq.o)
    assertEquals(1, pq.s)
  }

  @Test
  fun `success create empty page`() {
    val empty = Pq.empty()
    assertEquals(0, empty.o)
    assertEquals(0, empty.s)
  }

  @Test
  fun `success create un page param`() {
    val pq = Pq.unPage()
    assertEquals(0, pq.o)
    assertEquals(Int.MAX_VALUE, pq.s)
  }

  @Test
  fun `success create page param`() {
    val pq = Pq[0, 42]
    assertEquals(0, pq.o)
    assertEquals(42, pq.s)
  }
}
