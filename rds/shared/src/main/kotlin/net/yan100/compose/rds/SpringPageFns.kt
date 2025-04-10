package net.yan100.compose.rds

import net.yan100.compose.Pq
import net.yan100.compose.Pr
import net.yan100.compose.domain.IPage
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

/** # 对分页结果的封装，使得其返回包装对象 */
@Deprecated(
  "建议使用更符合 kotlin 语义 的 toPr 或者 toIPage",
  replaceWith = ReplaceWith("toPr()", "net.yan100.compose.rds.toPr"),
)
val <T : Any> Page<T>.result: Pr<T>
  get() {
    return if (totalElements == 0L) Pr.emptyWith()
    else if (pageable.isUnpaged) Pr.unPage(content)
    else {
      Pr[content, totalElements, totalPages]
    }
  }

@Suppress("DEPRECATION") fun <T : Any> Page<T>.toPr(): Pr<T> = result

fun <T : Any> Page<T>.toIPage(): IPage<T> = toPr()

/** # 对分页参数的封装，返回一个包装的对象 */
@Deprecated(
  "建议使用更符合 kotlin 语义 的 toPageable()",
  ReplaceWith("toPageable()", "net.yan100.compose.rds.toPageable"),
)
val Pq?.page: Pageable
  get() =
    PageRequest.of(
      (this?.o ?: Pq.MIN_OFFSET).toInt(),
      this?.s ?: Pq.MAX_PAGE_SIZE,
    )

/** @see [org.springframework.data.domain.PageRequest] */
@Suppress("DEPRECATION") fun Pq?.toPageable(): Pageable = page
