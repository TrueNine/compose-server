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
import net.yan100.compose.rds.crud.entities.jpa.RolePermissions
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Primary
@Repository
interface IRolePermissionsRepo : IRepo<RolePermissions> {
  fun findByRoleIdAndPermissionsId(roleId: RefId, permissionsId: RefId): RolePermissions?

  @Query("select rp.permissionsId from RolePermissions rp")
  fun findAllPermissionsIdByRoleId(roleId: RefId): Set<RefId>

  fun findAllByRoleId(roleId: RefId): List<RolePermissions>

  fun existsByRoleIdAndPermissionsId(roleId: RefId, permissionsId: RefId): Boolean

  fun deleteByRoleIdAndPermissionsId(roleId: RefId, permissionsId: RefId)

  fun deleteAllByPermissionsIdInAndRoleId(permissionsIds: List<RefId>, roleId: RefId)
}
