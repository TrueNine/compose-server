package io.github.truenine.composeserver.rds

import io.github.truenine.composeserver.Pq
import kotlin.test.Test
import kotlin.test.assertEquals
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl

class SpringPageFnsTest {
  @Test
  fun `success convert to pr`() {
    val springPage = PageImpl(listOf(1, 2, 3), Pq[0, 42].toPageable(), 3)
    assertEquals(3, springPage.content.size)
    assertEquals(42, springPage.size)
    assertEquals(1, springPage.totalPages)
    assertEquals(3, springPage.totalElements)

    val converted = springPage.toPr()
    assertEquals(3, converted.d.size)
    assertEquals(3, converted.t)
    assertEquals(1, converted.p)
    assertEquals(null, converted.o)
    assertEquals(null, converted.pageParam)
  }

  @Test
  fun `success convert empty spring page to pr`() {
    val springPage = Page.empty<Any>()
    val pr = springPage.toPr()
    assertEquals(0, pr.d.size)
    assertEquals(0, pr.t)
    assertEquals(0, pr.p)
    assertEquals(null, pr.pageParam)
  }
}
