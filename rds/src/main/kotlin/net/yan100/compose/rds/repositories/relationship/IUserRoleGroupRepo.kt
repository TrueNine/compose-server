/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
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
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.rds.repositories.relationship

import net.yan100.compose.rds.entities.relationship.UserRoleGroup
import net.yan100.compose.rds.repositories.base.IRepo
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface IUserRoleGroupRepo : IRepo<UserRoleGroup> {
  fun findByUserIdAndRoleGroupId(userId: String, roleGroupId: String): UserRoleGroup?

  fun findAllByUserId(userId: String): List<UserRoleGroup>

  @Query(
    """
    SELECT ur.roleGroupId 
    FROM UserRoleGroup ur
    WHERE ur.userId = :userId
  """
  )
  fun findAllRoleGroupIdByUserId(userId: String): Set<String>

  fun existsByUserIdAndRoleGroupId(userId: String, roleId: String): Boolean

  fun deleteAllByRoleGroupIdAndUserId(roleGroupId: String, userId: String)

  fun deleteAllByRoleGroupIdInAndUserId(roleGroupIds: List<String>, userId: String)

  fun deleteAllByUserId(userId: String)
}
