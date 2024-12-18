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
package net.yan100.compose.rds.crud.repositories.jpa

import net.yan100.compose.core.RefId
import net.yan100.compose.rds.core.IRepo
import net.yan100.compose.rds.crud.entities.jpa.UserRoleGroup
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Primary
@Repository
interface IUserRoleGroupRepo : IRepo<UserRoleGroup> {
  fun findByUserIdAndRoleGroupId(userId: RefId, roleGroupId: RefId): UserRoleGroup?

  fun findAllByUserId(userId: RefId): List<UserRoleGroup>

  @Query(
    """
    SELECT ur.roleGroupId 
    FROM UserRoleGroup ur
    WHERE ur.userId = :userId
  """
  )
  fun findAllRoleGroupIdByUserId(userId: RefId): Set<RefId>

  fun existsByUserIdAndRoleGroupId(userId: RefId, roleId: RefId): Boolean

  fun deleteAllByRoleGroupIdAndUserId(roleGroupId: RefId, userId: RefId)

  fun deleteAllByRoleGroupIdInAndUserId(roleGroupIds: List<RefId>, userId: RefId)

  fun deleteAllByUserId(userId: RefId)
}
