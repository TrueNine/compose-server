package io.github.truenine.composeserver.rds

import io.github.truenine.composeserver.Pq
import io.github.truenine.composeserver.Pr
import io.github.truenine.composeserver.domain.IPage
import io.github.truenine.composeserver.toSafeInt
import java.sql.Connection
import kotlin.reflect.KClass
import org.babyfish.jimmer.Page
import org.babyfish.jimmer.View
import org.babyfish.jimmer.sql.fetcher.DtoMetadata
import org.babyfish.jimmer.sql.fetcher.Fetcher
import org.babyfish.jimmer.sql.kt.ast.query.KConfigurableRootQuery

/** ## Convert jimmer Page to custom page */
fun <E : Any> Page<E>.toPr(): IPage<E> {
  return Pr[rows, totalRowCount, totalPageCount.toSafeInt()]
}

/**
 * ## Custom paginated query
 *
 * @param pq pagination parameters
 * @param conn database connection
 */
fun <E : Any> KConfigurableRootQuery<*, E>.fetchPq(pq: Pq? = Pq.DEFAULT_MAX, conn: Connection? = null): IPage<E> {
  return fetchPage((pq?.o ?: Pq.MIN_OFFSET), (pq?.s ?: Pq.MAX_PAGE_SIZE), conn).toPr()
}

/**
 * ## Custom paginated query
 *
 * @param pq pagination parameters
 * @param conn database connection
 * @param transformer result transformer
 */
fun <E : Any, R : Any> KConfigurableRootQuery<*, E>.fetchPq(pq: Pq? = Pq.DEFAULT_MAX, conn: Connection? = null, transformer: (E) -> R): IPage<R> {
  return fetchPage((pq?.o ?: Pq.MIN_OFFSET), (pq?.s ?: Pq.MAX_PAGE_SIZE), conn).toPr().transferTo(transformer)
}

/** ## Get a fetcher instance from View::class */
fun <R, S : View<R>> KClass<S>.toFetcher(): Fetcher<R> {
  return DtoMetadata.of(this.java).fetcher
}
