package net.yan100.compose.rds

import net.yan100.compose.rds.entities.IJpaEntity
import net.yan100.compose.rds.service.IBaseCrudService
import net.yan100.compose.rds.service.IMergeEntityEventService

/**
 * ## 统一通用查询服务接口
 *
 * ### 使用范例
 *
 * ```kotlin
 * @Service
 * class UserServiceImpl(
 *   private val userRepo: IUsrRepo,
 *   private val fullRepo: IFullUserRepo
 * ) : IUserService, ICrud<Usr> by jpa(userRepo) {
 * ```
 *
 * @see [jpa]
 */
interface ICrud<T : IJpaEntity> :
  IBaseCrudService<T, IRepo<T>>, IMergeEntityEventService<T>
