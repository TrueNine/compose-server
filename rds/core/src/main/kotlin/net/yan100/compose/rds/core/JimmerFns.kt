package net.yan100.compose.rds.core

import net.yan100.compose.core.Pq
import net.yan100.compose.core.Pr
import net.yan100.compose.core.domain.IPage
import net.yan100.compose.core.toSafeInt
import org.babyfish.jimmer.Page
import org.babyfish.jimmer.sql.kt.ast.query.KConfigurableRootQuery
import java.sql.Connection

fun <E : Any> Page<E>.toPr(): IPage<E> {
  return Pr[
    rows,
    totalRowCount,
    totalPageCount.toSafeInt()
  ]
}


fun <E : Any> KConfigurableRootQuery<*, E>.fetchPq(pq: Pq? = Pq.DEFAULT_MAX, conn: Connection? = null): IPage<E> {
  return fetchPage(
    (pq?.o ?: Pq.MIN_OFFSET).toSafeInt(),
    (pq?.s ?: Pq.MAX_PAGE_SIZE),
    conn
  ).toPr()
}


