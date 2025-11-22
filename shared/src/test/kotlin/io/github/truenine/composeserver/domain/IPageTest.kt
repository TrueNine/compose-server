package io.github.truenine.composeserver.domain

import io.github.truenine.composeserver.Pr
import kotlin.test.Test
import kotlin.test.assertEquals

class IPageTest {
  @Test
  fun `success create empty page`() {
    val empty = Pr.empty()
    assertEquals(0, empty.d.size)
    assertEquals(0, empty.p)
    assertEquals(0, empty.t)
    assertEquals(null, empty.o)
  }

  @Test
  fun `success create one page`() {
    val empty = Pr.one(1)
    assertEquals(1, empty.p)
    assertEquals(1, empty.t)
    assertEquals(1, empty.d.size)
    assertEquals(null, empty.pageParam)
    assertEquals(null, empty.o)
    assertEquals(1, empty.d.toList()[0])
  }

  @Test
  fun `success create pr`() {
    val pr = Pr[listOf(1, 2, 3), 100, 2]
    assertEquals(3, pr.d.size)
    assertEquals(100, pr.t)
    assertEquals(2, pr.p)
    assertEquals(null, pr.pageParam, "Page parameter should be null when not specified")
    assertEquals(null, pr.o)
  }
}
