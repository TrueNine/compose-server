package net.yan100.compose.rds.service

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Transient
import net.yan100.compose.rds.annotations.ACID
import net.yan100.compose.rds.entities.IJpaEntity
import net.yan100.compose.rds.entities.IJpaPersistentEntity
import net.yan100.compose.rds.event.MergeDataBaseEntityEvent
import net.yan100.compose.slf4j
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ApplicationListener
import kotlin.reflect.KClass

private val log = slf4j<IMergeEntityEventService<*>>()

@Suppress("DEPRECATION")
interface IMergeEntityEventService<T : IJpaEntity> :
  ApplicationListener<MergeDataBaseEntityEvent<*>> {
  data class MergeData<D : IJpaPersistentEntity>(
    val from: D,
    val to: D,
    val type: KClass<out IJpaPersistentEntity>,
  )

  /** ## 发布订阅调度器 */
  @get:JsonIgnore
  @get:Transient
  val mergeEntityEventPublisher: ApplicationEventPublisher

  /** ## 支持的合并类型 */
  @get:JsonIgnore
  @get:Transient
  val supportedMergeTypes: List<KClass<out IJpaPersistentEntity>>
    get() = emptyList()

  /**
   * ## 判断是否支持合并
   *
   * 实现类可自行决定实现逻辑
   */
  @Deprecated("需保存 support 路径以提高效率", replaceWith = ReplaceWith(""))
  fun supportedMergeEntityEvent(
    data: MergeData<out IJpaPersistentEntity>,
  ): Boolean {
    return supportedMergeTypes.isNotEmpty() &&
      supportedMergeTypes.any { it.isInstance(data.from) }
  }

  /** ## 服务方法的的合并实现 */
  @Suppress("UNCHECKED_CAST")
  fun mergeEntityEventProcessor(data: MergeData<out IJpaPersistentEntity>): T =
    data.from as T

  /** ## 内部调用进行 spring 事件发布 */
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
   * 注意：此方法的返回值为 data.to 的原始，并非 保存到数据库后的值 返回此参数的意义在于是否执行事件发布后，成功进行了合并操作
   *
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
