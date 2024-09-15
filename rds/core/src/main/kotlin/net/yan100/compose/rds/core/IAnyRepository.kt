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

import net.yan100.compose.core.Id
import net.yan100.compose.rds.core.entities.IAnyEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.NoRepositoryBean

/**
 * # 任意实体通用 CRUD 接口
 *
 * @author TrueNine
 * @since 2023-05-05
 */
@NoRepositoryBean
interface IAnyRepository<T : IAnyEntity> {
  /**
   * ## 根据 ID 查询 ID
   *
   * 这个查询存在的意义在于，当想获取一个存在的 id 时，可
   * 以一次查询完成，而不必进行其他操作
   *
   * @param id 主键 id
   */
  @Query("select e.id from #{#entityName} e where e.id = :id")
  fun findIdOrNullById(id: Id): Id?

  /**
   * ## 根据 ID 批量查询 ID
   *
   * 这个查询存在的意义在于，当想获取一个存在的 id 时，可
   * 以一次查询完成，而不必进行其他操作
   *
   * @param ids 主键 id
   */
  @Query("select e.id from #{#entityName} e where e.id in :ids")
  fun findAllIdById(ids: List<Id>): List<Id>
}


