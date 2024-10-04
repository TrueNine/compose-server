package net.yan100.compose.rds.core

import net.yan100.compose.core.Pq
import net.yan100.compose.testtookit.log
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class SpringPageFnsTest {

  @Test
  fun `un paged not exception`() {
    val pq = Pq[1, 2, false]
    log.info("pq: {}", pq)
    val pqPage = pq.toPageable()
    log.info("pqPage: {}", pqPage)

    assertIs<PageRequest>(pqPage)

    val upq = Pq.empty()
    val pg = upq.toPageable()
    val uclass = Pageable.unpaged()::class
    assertEquals(pg::class, uclass)
    assertTrue(pg.isUnpaged)
    val uResult = Page.empty<Any>(pg).toPr()
    log.info("uResult: {}", uResult)
  }
}
