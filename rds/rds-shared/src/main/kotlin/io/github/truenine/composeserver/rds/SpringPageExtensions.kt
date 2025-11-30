package io.github.truenine.composeserver.rds

import io.github.truenine.composeserver.Pq
import io.github.truenine.composeserver.Pr
import io.github.truenine.composeserver.domain.IPage
import org.springframework.data.domain.*

/** # Wrap paging result into a wrapper object */
@Deprecated("Use toPr or toIPage which are more idiomatic for Kotlin", replaceWith = ReplaceWith("toPr()", "io.github.truenine.composeserver.rds.toPr"))
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

/** # Wrap paging parameters and return a wrapper object */
@Deprecated("Use toPageable() which is more idiomatic for Kotlin", ReplaceWith("toPageable()", "io.github.truenine.composeserver.rds.toPageable"))
val Pq?.page: Pageable
  get() = PageRequest.of((this?.o ?: Pq.MIN_OFFSET).toInt(), this?.s ?: Pq.MAX_PAGE_SIZE)

/** @see [org.springframework.data.domain.PageRequest] */
@Suppress("DEPRECATION") fun Pq?.toPageable(): Pageable = page
