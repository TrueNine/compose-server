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

import net.yan100.compose.core.alias.ReferenceId
import net.yan100.compose.rds.entities.relationship.RoleGroupRole
import net.yan100.compose.rds.entities.relationship.RolePermissions
import net.yan100.compose.rds.entities.relationship.UserRoleGroup

/** # 角色权限管理器 */
interface IRbacAggregator {
  fun findAllRoleNameByUserAccount(account: String): Set<String>

  fun findAllPermissionsNameByUserAccount(account: String): Set<String>

  fun findAllSecurityNameByUserId(userId: ReferenceId): Set<String>

  fun findAllSecurityNameByAccount(account: String): Set<String>

  fun saveRoleGroupToUser(roleGroupId: ReferenceId, userId: ReferenceId): UserRoleGroup?

  fun saveAllRoleGroupToUser(roleGroupIds: List<ReferenceId>, userId: ReferenceId): List<UserRoleGroup>

  fun revokeRoleGroupFromUser(roleGroupId: ReferenceId, userId: ReferenceId)

  fun revokeAllRoleGroupFromUser(roleGroupIds: List<ReferenceId>, userId: ReferenceId)

  fun saveRoleToRoleGroup(roleId: ReferenceId, roleGroupId: ReferenceId): RoleGroupRole?

  fun saveAllRoleToRoleGroup(roleIds: List<ReferenceId>, roleGroupId: ReferenceId): List<RoleGroupRole>

  fun revokeRoleFromRoleGroup(roleId: ReferenceId, roleGroupId: ReferenceId)

  fun revokeAllRoleFromRoleGroup(roleIds: List<ReferenceId>, roleGroupId: ReferenceId)

  fun savePermissionsToRole(permissionsId: ReferenceId, roleId: ReferenceId): RolePermissions?

  fun saveAllPermissionsToRole(permissionsIds: List<ReferenceId>, roleId: ReferenceId): List<RolePermissions>

  fun revokePermissionsFromRole(permissionsId: ReferenceId, roleId: ReferenceId)

  fun revokeAllPermissionsFromRole(permissionsIds: List<ReferenceId>, roleId: ReferenceId)
}
