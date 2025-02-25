package net.yan100.compose.rds.core

import java.sql.Connection
import kotlin.reflect.KClass
import net.yan100.compose.core.Pq
import net.yan100.compose.core.Pr
import net.yan100.compose.core.domain.IPage
import net.yan100.compose.core.toSafeInt
import org.babyfish.jimmer.Page
import org.babyfish.jimmer.View
import org.babyfish.jimmer.sql.fetcher.DtoMetadata
import org.babyfish.jimmer.sql.fetcher.Fetcher
import org.babyfish.jimmer.sql.kt.ast.query.KConfigurableRootQuery

/** ## 将 jimmer page 转换为 自定义 page */
fun <E : Any> Page<E>.toPr(): IPage<E> {
  return Pr[rows, totalRowCount, totalPageCount.toSafeInt()]
}

/**
 * ## 自定义 分页查询
 *
 * @param pq 分页参数
 */
fun <E : Any> KConfigurableRootQuery<*, E>.fetchPq(
  pq: Pq? = Pq.DEFAULT_MAX,
  conn: Connection? = null,
): IPage<E> {
  return fetchPage(
      (pq?.o ?: Pq.MIN_OFFSET).toSafeInt(),
      (pq?.s ?: Pq.MAX_PAGE_SIZE),
      conn,
    )
    .toPr()
}

/** ## 从 View::class 获取 一个 fetcher 实例 */
fun <R, S : View<R>> KClass<S>.toFetcher(): Fetcher<R> {
  return DtoMetadata.of(this.java).fetcher
}
