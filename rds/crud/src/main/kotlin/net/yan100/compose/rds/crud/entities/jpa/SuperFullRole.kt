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
package net.yan100.compose.rds.crud.entities.jpa

import jakarta.persistence.*
import net.yan100.compose.core.consts.IDbNames
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.crud.entities.jpa.Permissions
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction


@MetaDef(shadow = true)
interface SuperFullRole : SuperRole {

  /** 权限 */
  @get:ManyToMany(fetch = FetchType.EAGER, targetEntity = Permissions::class)
  @get:JoinTable(
    name = RolePermissions.TABLE_NAME,
    joinColumns =
      [
        JoinColumn(
          table = RolePermissions.TABLE_NAME,
          name = RolePermissions.ROLE_ID,
          referencedColumnName = IDbNames.ID,
          foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT),
          insertable = false,
          updatable = false,
        )
      ],
    inverseJoinColumns =
      [
        JoinColumn(
          table = RolePermissions.TABLE_NAME,
          name = RolePermissions.PERMISSIONS_ID,
          referencedColumnName = IDbNames.ID,
          foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT),
          insertable = false,
          updatable = false,
        )
      ],
    foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT),
  )
  @get:Fetch(FetchMode.SUBSELECT)
  @get:NotFound(action = NotFoundAction.IGNORE)
  var permissions: MutableList<@JvmSuppressWildcards Permissions>
}
