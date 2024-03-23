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

import net.yan100.compose.rds.entities.FullRoleGroup
import net.yan100.compose.rds.entities.RoleGroup
import net.yan100.compose.rds.repositories.base.IRepo
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface RoleGroupRepo : IRepo<RoleGroup> {
  fun findAllByName(name: String): List<RoleGroup>

  @Query("""
    from RoleGroup rg
    left join UserRoleGroup ur on rg.id = ur.roleGroupId
    where ur.userId = :userId
  """)
  fun findAllByUserId(userId: Long): List<RoleGroup>
}

@Repository
interface FullRoleGroupEntityRepo : IRepo<FullRoleGroup> {
  fun findAllByName(name: String): List<FullRoleGroup>

  @Query("""
    from FullRoleGroup rg
    left join UserRoleGroup ur on rg.id = ur.roleGroupId
    where ur.userId = :userId
  """)
  fun findAllByUserId(userId: Long): List<FullRoleGroup>
}
