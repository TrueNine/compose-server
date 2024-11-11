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
package net.yan100.compose.rds.service.aggregator

import net.yan100.compose.core.RefId
import net.yan100.compose.core.ReferenceId
import net.yan100.compose.rds.entities.RoleGroupRole
import net.yan100.compose.rds.entities.RolePermissions
import net.yan100.compose.rds.entities.UserRoleGroup

/** # 角色权限管理器 */
interface IRbacAggregator {
  fun fetchAllRoleNameByUserAccount(account: String): Set<String>

  fun findAllPermissionsNameByUserAccount(account: String): Set<String>

  fun findAllSecurityNameByUserId(userId: RefId): Set<String>

  fun findAllSecurityNameByAccount(account: String): Set<String>

  fun saveRoleGroupToUser(roleGroupId: ReferenceId, userId: RefId): UserRoleGroup?

  fun saveAllRoleGroupToUser(roleGroupIds: List<ReferenceId>, userId: RefId): List<UserRoleGroup>

  fun revokeRoleGroupFromUser(roleGroupId: ReferenceId, userId: RefId)

  fun revokeAllRoleGroupFromUser(roleGroupIds: List<ReferenceId>, userId: RefId)

  fun linkRoleToRoleGroup(roleId: ReferenceId, roleGroupId: ReferenceId): RoleGroupRole?

  fun linkAllRoleToRoleGroup(roleIds: List<ReferenceId>, roleGroupId: ReferenceId): List<RoleGroupRole>

  fun revokeRoleFromRoleGroup(roleId: ReferenceId, roleGroupId: ReferenceId)

  fun revokeAllRoleFromRoleGroup(roleIds: List<ReferenceId>, roleGroupId: ReferenceId)

  fun savePermissionsToRole(permissionsId: ReferenceId, roleId: ReferenceId): RolePermissions?

  fun saveAllPermissionsToRole(permissionsIds: List<ReferenceId>, roleId: ReferenceId): List<RolePermissions>

  fun revokePermissionsFromRole(permissionsId: ReferenceId, roleId: ReferenceId)

  fun revokeAllPermissionsFromRole(permissionsIds: List<ReferenceId>, roleId: ReferenceId)
}
