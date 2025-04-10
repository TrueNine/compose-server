package net.yan100.compose.rds.service

import kotlin.reflect.KClass
import net.yan100.compose.holders.EventPublisherHolder
import net.yan100.compose.rds.entities.IJpaEntity
import net.yan100.compose.rds.entities.IJpaPersistentEntity
import org.springframework.context.ApplicationEventPublisher

open class MergeEntityEventServiceDefaultImpl<T : IJpaEntity>(
  private val supportedTypes: List<KClass<out IJpaPersistentEntity>> =
    emptyList()
) : IMergeEntityEventService<T> {
  override val supportedMergeTypes: List<KClass<out IJpaPersistentEntity>>
    get() = this.supportedTypes

  /** ## 默认的 [ApplicationEventPublisher] 获取器 */
  override val mergeEntityEventPublisher: ApplicationEventPublisher
    get() = EventPublisherHolder.content
}
