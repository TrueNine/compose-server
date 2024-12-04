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
package net.yan100.compose.rds.crud.service.aggregator

import net.yan100.compose.core.RefId
import net.yan100.compose.rds.crud.entities.jpa.RoleGroupRole
import net.yan100.compose.rds.crud.entities.jpa.RolePermissions
import net.yan100.compose.rds.crud.entities.jpa.UserRoleGroup

/** # 角色权限管理器 */
interface IRbacAggregator {
  fun fetchAllRoleNameByUserAccount(account: String): Set<String>

  fun findAllPermissionsNameByUserAccount(account: String): Set<String>

  fun findAllSecurityNameByUserId(userId: RefId): Set<String>

  fun findAllSecurityNameByAccount(account: String): Set<String>

  fun saveRoleGroupToUser(roleGroupId: RefId, userId: RefId): UserRoleGroup?

  fun saveAllRoleGroupToUser(roleGroupIds: List<RefId>, userId: RefId): List<UserRoleGroup>

  fun revokeRoleGroupFromUser(roleGroupId: RefId, userId: RefId)

  fun revokeAllRoleGroupFromUser(roleGroupIds: List<RefId>, userId: RefId)

  fun linkRoleToRoleGroup(roleId: RefId, roleGroupId: RefId): RoleGroupRole?

  fun linkAllRoleToRoleGroup(roleIds: List<RefId>, roleGroupId: RefId): List<RoleGroupRole>

  fun revokeRoleFromRoleGroup(roleId: RefId, roleGroupId: RefId)

  fun revokeAllRoleFromRoleGroup(roleIds: List<RefId>, roleGroupId: RefId)

  fun savePermissionsToRole(permissionsId: RefId, roleId: RefId): RolePermissions?

  fun saveAllPermissionsToRole(permissionsIds: List<RefId>, roleId: RefId): List<RolePermissions>

  fun revokePermissionsFromRole(permissionsId: RefId, roleId: RefId)

  fun revokeAllPermissionsFromRole(permissionsIds: List<RefId>, roleId: RefId)
}
