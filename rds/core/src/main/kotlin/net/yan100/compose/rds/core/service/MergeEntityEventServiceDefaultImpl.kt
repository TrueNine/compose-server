package net.yan100.compose.rds.core.service

import net.yan100.compose.core.holders.EventPublisherHolder
import net.yan100.compose.rds.core.entities.IAnyEntity
import net.yan100.compose.rds.core.entities.IEntity
import org.springframework.context.ApplicationEventPublisher
import kotlin.reflect.KClass

open class MergeEntityEventServiceDefaultImpl<T : IEntity>(
  private val supportedTypes: List<KClass<out IAnyEntity>> = emptyList()
) : IMergeEntityEventService<T> {
  override val supportedMergeTypes: List<KClass<out IAnyEntity>> get() = this.supportedTypes

  /**
   * ## 默认的 [ApplicationEventPublisher] 获取器
   */
  override val mergeEntityEventPublisher: ApplicationEventPublisher get() = EventPublisherHolder.content
}
