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
package net.yan100.compose.rds.repositories

import net.yan100.compose.rds.entities.FullRole
import net.yan100.compose.rds.entities.Role
import net.yan100.compose.rds.repositories.base.IRepo
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository : IRepo<Role> {
  fun findAllByName(name: String): List<Role>

  @Query(
    """
    from Role r
    left join RoleGroupRole rgr on r.id = rgr.roleId
    left join UserRoleGroup urg on rgr.roleGroupId = urg.roleGroupId
    where urg.userId = :userId
  """
  )
  fun findAllByUserId(userId: Long): List<Role>
}

@Repository
interface AllRoleEntityRepository : IRepo<FullRole> {
  fun findAllByName(name: String): List<FullRole>

  @Query(
    """
    from FullRole r
    left join RoleGroupRole rgr on r.id = rgr.roleId
    left join UserRoleGroup urg on rgr.roleGroupId = urg.roleGroupId
    where urg.userId = :userId
  """
  )
  fun findAllByUserId(userId: Long): List<FullRole>
}
