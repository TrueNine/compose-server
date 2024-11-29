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

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED
import jakarta.persistence.*
import jakarta.persistence.ConstraintMode.NO_CONSTRAINT
import jakarta.persistence.FetchType.EAGER
import net.yan100.compose.ksp.core.annotations.MetaDef
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode.SUBSELECT
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction.IGNORE


@MetaDef(shadow = true)
@MappedSuperclass
abstract class SuperFullUsr : SuperUsr() {
  /** 角色组 */
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Schema(title = "角色组", requiredMode = NOT_REQUIRED)
  @ManyToMany(fetch = EAGER, targetEntity = RoleGroup::class)
  @JoinTable(
    name = UserRoleGroup.TABLE_NAME,
    joinColumns =
      [JoinColumn(name = UserRoleGroup.USER_ID, referencedColumnName = ID, foreignKey = ForeignKey(NO_CONSTRAINT), insertable = false, updatable = false)],
    inverseJoinColumns =
      [
        JoinColumn(name = UserRoleGroup.ROLE_GROUP_ID, referencedColumnName = ID, foreignKey = ForeignKey(NO_CONSTRAINT), insertable = false, updatable = false)
      ],
    foreignKey = ForeignKey(NO_CONSTRAINT),
  )
  @Fetch(SUBSELECT)
  @NotFound(action = IGNORE)
  open var roleGroups: List<RoleGroup> = mutableListOf()
}
