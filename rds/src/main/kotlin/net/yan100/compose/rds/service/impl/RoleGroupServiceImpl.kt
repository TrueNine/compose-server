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
package net.yan100.compose.rds.service.impl

import net.yan100.compose.core.consts.DataBaseBasicFieldNames
import net.yan100.compose.rds.entities.RoleGroup
import net.yan100.compose.rds.repositories.relationship.IUserRoleGroupRepo
import net.yan100.compose.rds.service.IRoleGroupService
import net.yan100.compose.rds.service.base.CrudService
import org.springframework.stereotype.Service

@Service
class RoleGroupServiceImpl(
  private val rgRepo: net.yan100.compose.rds.repositories.RoleGroupRepo,
  private val urRepo: IUserRoleGroupRepo
) : IRoleGroupService, CrudService<RoleGroup>(rgRepo) {
  override fun assignRootToUser(
    userId: String
  ): net.yan100.compose.rds.entities.relationship.UserRoleGroup {
    return net.yan100.compose.rds.entities.relationship
      .UserRoleGroup()
      .apply {
        this.roleGroupId = DataBaseBasicFieldNames.Rbac.ROOT_ID_STR
        this.userId = userId
      }
      .let { urRepo.save(it) }
  }

  override fun assignPlainToUser(
    userId: String
  ): net.yan100.compose.rds.entities.relationship.UserRoleGroup {
    return net.yan100.compose.rds.entities.relationship
      .UserRoleGroup()
      .apply {
        this.roleGroupId = DataBaseBasicFieldNames.Rbac.USER_ID_STR
        this.userId = userId
      }
      .let { urRepo.save(it) }
  }

  override fun assignAdminToUser(
    userId: String
  ): net.yan100.compose.rds.entities.relationship.UserRoleGroup {
    return net.yan100.compose.rds.entities.relationship
      .UserRoleGroup()
      .apply {
        this.roleGroupId = DataBaseBasicFieldNames.Rbac.ADMIN_ID_STR
        this.userId = userId
      }
      .let { urRepo.save(it) }
  }
}
