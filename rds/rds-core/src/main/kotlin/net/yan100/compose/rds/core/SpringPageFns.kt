package net.yan100.compose.rds.core

import net.yan100.compose.core.Pr
import org.springframework.data.domain.Page

/** # 对分页结果的封装，使得其返回包装对象 */
val <T> Page<T>.result: Pr<T>
  get() = JpaPagedWrapper.result(this)

/** # 封装一个新 的 集合到分页结果 */
fun <T, R> Page<T>.resultByNewList(newList: List<R>): Pr<R> = JpaPagedWrapper.resultByNewList(this, newList)
