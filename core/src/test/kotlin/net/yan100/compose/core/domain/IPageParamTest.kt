package net.yan100.compose.core.domain

import net.yan100.compose.core.Pq
import kotlin.test.Test
import kotlin.test.assertEquals

class IPageParamTest {
  @Test
  fun `success get from like empty`() {
    val e = Pq[object : IPageParamLike {

    }]
    assertEquals(Pq.MIN_OFFSET, e.o)
    assertEquals(Pq.MAX_PAGE_SIZE, e.s)
  }

  @Test
  fun `fail create pq`() {
    val pq = Pq[-1, -13]
    assertEquals(Pq.MIN_OFFSET, pq.o)
    assertEquals(1, pq.s)
    assertEquals(false, pq.u)
  }

  @Test
  fun `success create empty page`() {
    val empty = Pq.empty()
    assertEquals(0, empty.o)
    assertEquals(0, empty.s)
    assertEquals(true, empty.u)
  }

  @Test
  fun `success create un page param`() {
    val pq = Pq.unPage()
    assertEquals(0, pq.o)
    assertEquals(Int.MAX_VALUE, pq.s)
    assertEquals(true, pq.u)
  }

  @Test
  fun `success create page param`() {
    val pq = Pq[0, 42]
    assertEquals(0, pq.o)
    assertEquals(42, pq.s)
    assertEquals(false, pq.u)
  }
}
