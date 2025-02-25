package net.yan100.compose.rds.core.service

import kotlin.reflect.KClass
import net.yan100.compose.rds.core.ICrud
import net.yan100.compose.rds.core.IRepo
import net.yan100.compose.rds.core.entities.IJpaEntity
import net.yan100.compose.rds.core.entities.IJpaPersistentEntity

/**
 * # 统一查询接口委托实现
 *
 * @param repo Spring Data JPA Repository
 * @param supportedTypes 级联合并操作所需关联的类型
 * @see [net.yan100.compose.rds.core.jpa]
 */
open class SpringBootStarterDataJpaCrudServiceDelegateProvider<
  T : IJpaEntity,
  R : IRepo<T>,
>
@Deprecated(
  message = "不建议直接创建此类，请使用其他方式",
  replaceWith = ReplaceWith("jpa", "net.yan100.compose.rds.core.jpa"),
  level = DeprecationLevel.ERROR,
)
constructor(
  override val repo: R,
  supportedTypes: List<KClass<out IJpaPersistentEntity>> = emptyList(),
) :
  ICrud<T>,
  IBaseCrudService<T, IRepo<T>>,
  IMergeEntityEventService<T> by MergeEntityEventServiceDefaultImpl(
    supportedTypes
  )
