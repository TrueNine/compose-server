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


import net.yan100.compose.rds.core.entities.IAnyEntity
import net.yan100.compose.rds.core.entities.IEntity
import kotlin.reflect.KClass

/**
 * # 统一查询接口委托实现
 *
 * @see [net.yan100.compose.rds.core.jpa]
 * @param repo Spring Data JPA Repository
 * @param supportedTypes 级联合并操作所需关联的类型
 */
@Deprecated(message = "不建议直接创建此类，请使用其他方式", replaceWith = ReplaceWith("jpa", "net.yan100.compose.rds.core.jpa"), level = DeprecationLevel.ERROR)
open class SpringBootStarterDataJpaCrudServiceDelegateProvider<T : IEntity, R : IRepo<T>>(
  override val repo: R, supportedTypes: List<KClass<out IAnyEntity>> = emptyList()
) : ICrud<T>, IBaseCrudService<T, IRepo<T>>, IMergeEntityEventService<T> by MergeEntityEventServiceDefaultImpl(supportedTypes)
