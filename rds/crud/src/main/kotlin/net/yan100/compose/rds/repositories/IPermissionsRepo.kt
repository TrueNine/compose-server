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

import net.yan100.compose.rds.core.IRepo
import net.yan100.compose.rds.entities.Permissions
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository


@Primary
@Repository
interface IPermissionsRepo : IRepo<Permissions> {
  fun findAllByName(name: String): List<Permissions>

  @Query(
    """
    FROM Permissions p
    LEFT JOIN RolePermissions rp on p.id = rp.permissionsId
    LEFT JOIN RoleGroupRole rgr on rp.roleId = rgr.roleId
    LEFT JOIN UserRoleGroup urg on rgr.roleGroupId = urg.roleGroupId
    WHERE urg.userId = :userId
  """
  )
  fun findAllByUserId(userId: String): List<Permissions>
}
