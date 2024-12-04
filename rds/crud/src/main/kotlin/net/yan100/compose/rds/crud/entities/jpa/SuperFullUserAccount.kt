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

import jakarta.persistence.ConstraintMode.NO_CONSTRAINT
import jakarta.persistence.FetchType.EAGER
import jakarta.persistence.ForeignKey
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import net.yan100.compose.core.consts.IDbNames
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.crud.entities.jpa.RoleGroup
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode.SUBSELECT
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction.IGNORE


@MetaDef(shadow = true)
interface SuperFullUserAccount : SuperUserAccount {
  /** 角色组 */
  @get:ManyToMany(fetch = EAGER, targetEntity = RoleGroup::class)
  @get:JoinTable(
    name = UserRoleGroup.TABLE_NAME,
    joinColumns =
      [JoinColumn(
        name = UserRoleGroup.USER_ID,
        referencedColumnName = IDbNames.ID,
        foreignKey = ForeignKey(NO_CONSTRAINT),
        insertable = false,
        updatable = false
      )],
    inverseJoinColumns =
      [
        JoinColumn(
          name = UserRoleGroup.ROLE_GROUP_ID,
          referencedColumnName = IDbNames.ID,
          foreignKey = ForeignKey(NO_CONSTRAINT),
          insertable = false,
          updatable = false
        )
      ],
    foreignKey = ForeignKey(NO_CONSTRAINT),
  )
  @get:Fetch(SUBSELECT)
  @get:NotFound(action = IGNORE)
  var roleGroups: MutableList<RoleGroup>
}
