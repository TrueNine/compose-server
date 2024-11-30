package net.yan100.compose.rds.jimmer

import net.yan100.compose.core.domain.IPage
import net.yan100.compose.core.domain.IPageParam
import org.babyfish.jimmer.Page

fun <T : Any> Page<T>.toIPage(pageParam: IPageParam? = null): IPage<T> {
  return IPage[this.rows, this.totalRowCount, pageParam]
}
