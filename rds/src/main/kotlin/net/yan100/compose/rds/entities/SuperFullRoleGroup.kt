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
package net.yan100.compose.rds.entities

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import net.yan100.compose.ksp.core.annotations.MetaDef
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction


@MetaDef(shadow = true)
@MappedSuperclass
abstract class SuperFullRoleGroup : SuperRoleGroup() {
  /** 角色 */
  @Schema(title = "角色", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @ManyToMany(fetch = FetchType.EAGER, targetEntity = FullRole::class)
  @JoinTable(
    name = RoleGroupRole.TABLE_NAME,
    joinColumns =
    [
      JoinColumn(
        table = RoleGroupRole.TABLE_NAME,
        name = RoleGroupRole.ROLE_GROUP_ID,
        referencedColumnName = ID,
        foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT),
        insertable = false,
        updatable = false,
      )
    ],
    inverseJoinColumns =
    [
      JoinColumn(
        table = RoleGroupRole.TABLE_NAME,
        name = RoleGroupRole.ROLE_ID,
        referencedColumnName = ID,
        foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT),
        insertable = false,
        updatable = false,
      )
    ],
    foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT),
  )
  @Fetch(FetchMode.SUBSELECT)
  @NotFound(action = NotFoundAction.IGNORE)
  var roles: List<@JvmSuppressWildcards FullRole> = mutableListOf()
}
