package net.yan100.compose.rds.jimmer

import net.yan100.compose.core.domain.IPageParam
import net.yan100.compose.core.toSafeInt
import org.babyfish.jimmer.Page
import org.babyfish.jimmer.sql.kt.ast.query.KConfigurableRootQuery
import java.sql.Connection


fun <E : Any, R> KConfigurableRootQuery<E, R>.fetchPage(pageParam: IPageParam, connection: Connection? = null): Page<R> {
  return fetchPage(
    pageParam.safeOffset.toSafeInt(),
    if (pageParam.u == true) Int.MAX_VALUE else pageParam.safePageSize,
    connection
  )
}
