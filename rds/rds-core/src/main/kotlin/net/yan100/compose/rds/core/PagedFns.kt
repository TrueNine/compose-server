package net.yan100.compose.rds.core

import net.yan100.compose.core.Pq
import org.springframework.data.domain.Pageable

/** # 对分页参数的封装，返回一个包装的对象 */
val Pq?.page: Pageable
  get() = JpaPagedWrapper.param(this)
