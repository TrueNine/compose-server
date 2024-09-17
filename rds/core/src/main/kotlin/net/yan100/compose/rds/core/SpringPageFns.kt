package net.yan100.compose.rds.core

import net.yan100.compose.core.Pq
import net.yan100.compose.core.Pr
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

/** # 对分页结果的封装，使得其返回包装对象 */
val <T : Any> Page<T>.result: Pr<T>
  get() {
    return Pr[
      content,
      totalElements,
      if (pageable.isPaged) pageable.offset else 0,
      if (pageable.isUnpaged) pageable.pageSize else content.size,
      pageable.isUnpaged
    ]
  }

/** # 对分页参数的封装，返回一个包装的对象 */
val Pq?.page: Pageable
  get() = if (this?.u == null || this.u == false) PageRequest.of((this?.o ?: Pq.MIN_OFFSET).toInt(), this?.s ?: Pq.MAX_PAGE_SIZE)
  else Pageable.unpaged()
