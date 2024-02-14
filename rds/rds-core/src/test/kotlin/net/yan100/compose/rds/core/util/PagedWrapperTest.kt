package net.yan100.compose.rds.core.util

import net.yan100.compose.rds.core.util.PagedWrapper.DEFAULT_MAX
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PagedWrapperTest {

    @Test
    fun testWrapBy() {
        val a =
            generateSequence(0) { it + 1 }
                .take(1000).map { it.toString() }
                .toList()
        val b = PagedWrapper.warpBy(DEFAULT_MAX) {
            a.asSequence()
        }.also {
            assertEquals(it.dataList.size, 42)
            assertEquals(it.size, 42)
            assertEquals(it.total, 1000)
            assertEquals(it.pageSize, 23)
            assertEquals(it.offset, 0)
        }
        println(b)
    }
}
