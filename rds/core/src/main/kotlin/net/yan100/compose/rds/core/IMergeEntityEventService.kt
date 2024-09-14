/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.rds.core

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Transient
import net.yan100.compose.core.slf4j
import net.yan100.compose.rds.core.annotations.ACID
import net.yan100.compose.rds.core.entities.IAnyEntity
import net.yan100.compose.rds.core.entities.IEntity
import net.yan100.compose.rds.core.event.MergeDataBaseEntityEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ApplicationListener
import kotlin.reflect.KClass

private val log = slf4j<IMergeEntityEventService<*>>()

@Suppress("DEPRECATION")
interface IMergeEntityEventService<T : IEntity> : ApplicationListener<MergeDataBaseEntityEvent<*>> {
  data class MergeData<D : IAnyEntity>(val from: D, val to: D, val type: KClass<out IAnyEntity>)

  /**
   * ## 发布订阅调度器
   */
  @get:JsonIgnore
  @get:Transient
  val mergeEntityEventPublisher: ApplicationEventPublisher

  /**
   * ## 支持的合并类型
   */
  @get:JsonIgnore
  @get:Transient
  val supportedMergeTypes: List<KClass<out IAnyEntity>>
    get() = emptyList()

  /**
   * ## 判断是否支持合并
   *
   * 实现类可自行决定实现逻辑
   */
  @Deprecated("需保存 support 路径以提高效率", replaceWith = ReplaceWith(""))
  fun supportedMergeEntityEvent(data: MergeData<out IAnyEntity>): Boolean {
    return supportedMergeTypes.isNotEmpty() && supportedMergeTypes.any { it.isInstance(data.from) }
  }

  /**
   * ## 服务方法的的合并实现
   */
  @Suppress("UNCHECKED_CAST")
  fun mergeEntityEventProcessor(data: MergeData<out IAnyEntity>): T = data.from as T

  /**
   * ## 内部调用进行 spring 事件发布
   */
  @Deprecated(message = "不建议使用此接口直接发出事件", level = DeprecationLevel.ERROR)
  override fun onApplicationEvent(event: MergeDataBaseEntityEvent<*>) {
    if (event.processed) return
    if (supportedMergeTypes.isEmpty()) return
    if (event.from == null || event.to == null) {
      event.processed = true
      return
    }
    if (event.from::class != event.to::class) {
      event.processed = true
      return
    }
    log.trace("merge from: this = {} {} to: {}", this, event.from, event.to)
    val data = MergeData(event.from, event.to, event.to::class)
    if (supportedMergeEntityEvent(data)) {
      mergeEntityEventProcessor(data)
      event.processed = true
    }
  }

  /**
   * ## 级联合并
   *
   * 触发级联合并事件
   *
   * 注意：此方法的返回值为 data.to 的原始，并非 保存到数据库后的值
   * 返回此参数的意义在于是否执行事件发布后，成功进行了合并操作
   * @see [onApplicationEvent]
   */
  @ACID
  fun cascadeMerge(from: T, to: T): T {
    val mergeEvent = MergeDataBaseEntityEvent(from, to)
    log.trace("merge event begin, class = {}", from::class)
    mergeEntityEventPublisher.publishEvent(mergeEvent)
    log.trace("merge event end")
    return mergeEvent.to!!
  }
}
