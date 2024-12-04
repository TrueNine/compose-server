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
package net.yan100.compose.rds.crud.service.impl

import net.yan100.compose.core.consts.IDbNames
import net.yan100.compose.rds.core.ICrud
import net.yan100.compose.rds.core.jpa
import net.yan100.compose.rds.crud.entities.jpa.RoleGroup
import net.yan100.compose.rds.crud.entities.jpa.UserRoleGroup
import net.yan100.compose.rds.crud.repositories.jpa.IRoleGroupRepo
import net.yan100.compose.rds.crud.repositories.jpa.IUserRoleGroupRepo
import org.springframework.stereotype.Service

@Service
class RoleGroupServiceImpl(
  private val rgRepo: IRoleGroupRepo,
  private val urRepo: IUserRoleGroupRepo
) : net.yan100.compose.rds.crud.service.IRoleGroupService, ICrud<RoleGroup> by jpa(rgRepo) {
  override fun assignRootToUser(userId: String): UserRoleGroup {
    return UserRoleGroup()
      .apply {
        this.roleGroupId = IDbNames.Rbac.ROOT_ID_STR
        this.userId = userId
      }
      .let { urRepo.save(it) }
  }

  override fun assignPlainToUser(userId: String): UserRoleGroup {
    return UserRoleGroup()
      .apply {
        this.roleGroupId = IDbNames.Rbac.USER_ID_STR
        this.userId = userId
      }
      .let { urRepo.save(it) }
  }

  override fun assignAdminToUser(userId: String): UserRoleGroup {
    return UserRoleGroup()
      .apply {
        this.roleGroupId = IDbNames.Rbac.ADMIN_ID_STR
        this.userId = userId
      }
      .let { urRepo.save(it) }
  }
}
