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
package net.yan100.compose.rds.service.base

import kotlin.reflect.KClass
import net.yan100.compose.core.alias.TODO
import net.yan100.compose.core.log.slf4j
import net.yan100.compose.rds.core.entities.IAnyEntity
import net.yan100.compose.rds.core.entities.IEntity
import net.yan100.compose.rds.events.MergeDataBaseEntityEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ApplicationListener

private val log = slf4j<IMergeEventService<*>>()

interface IMergeEventService<T : IEntity> : ApplicationListener<MergeDataBaseEntityEvent<*>> {
  data class MergeData<D : IAnyEntity>(val from: D, val to: D, val type: KClass<*>)

  val mergeEntityEventPublisher: ApplicationEventPublisher

  val supportedTypes: List<KClass<*>>
    get() = emptyList()

  @TODO("需保存 support 路径以提高效率")
  fun supportedMerge(data: MergeData<*>): Boolean {
    return supportedTypes.isNotEmpty() && supportedTypes.any { it.isInstance(data.from) }
  }

  @Suppress("UNCHECKED_CAST") fun persistMerge(data: MergeData<*>): T = data.from as T

  @Suppress("UNCHECKED_CAST")
  override fun onApplicationEvent(event: MergeDataBaseEntityEvent<*>) {
    if (event.processed) return
    if (supportedTypes.isEmpty()) return
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
    if (supportedMerge(data)) {
      persistMerge(data)
      event.processed = true
    }
  }

  fun cascadeMerge(from: T, to: T): T {
    val m = MergeDataBaseEntityEvent(from, to)
    log.trace("merge event begin, class = {}", from::class)
    mergeEntityEventPublisher.publishEvent(m)
    log.trace("merge event end")
    return m.to!!
  }
}
