package net.yan100.compose.rds.core.event

import net.yan100.compose.rds.core.entities.IJpaPersistentEntity
import org.springframework.context.ApplicationEvent

@Deprecated("难于维护")
data class MergeDataBaseEntityEvent<T : IJpaPersistentEntity>(
  val from: T?,
  val to: T?,
  var processed: Boolean = false,
) : ApplicationEvent(from!!) {
  init {
    check(!from!!.isNew) { "from entity must be database entity" }
    check(!to!!.isNew) { "to entity must be database entity" }
  }
}
