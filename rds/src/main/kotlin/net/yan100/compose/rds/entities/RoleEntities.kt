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
package net.yan100.compose.rds.entities

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.annotation.Nullable
import jakarta.persistence.*
import jakarta.persistence.ForeignKey
import jakarta.persistence.Table
import net.yan100.compose.rds.core.entities.IEntity
import net.yan100.compose.rds.entities.relationship.RolePermissions
import org.hibernate.annotations.*

@MappedSuperclass
abstract class SuperRole : IEntity() {
  companion object {
    const val TABLE_NAME = "role"
    const val NAME = "name"
    const val DOC = "doc"
  }

  /** 角色名称 */
  @Nullable @Schema(title = "角色名称") @Column(name = NAME) lateinit var name: String

  /** 角色描述 */
  @Nullable @Column(name = DOC) @Schema(title = "角色描述") var doc: String? = null
}

/**
 * 角色
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "角色")
@Table(name = SuperRole.TABLE_NAME)
class Role : SuperRole()

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = SuperRole.TABLE_NAME)
class FullRole : SuperRole() {

  /** 权限 */
  @Schema(title = "权限", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @ManyToMany(fetch = FetchType.EAGER, targetEntity = Permissions::class)
  @JoinTable(
    name = RolePermissions.TABLE_NAME,
    joinColumns =
      [
        JoinColumn(
          table = RolePermissions.TABLE_NAME,
          name = RolePermissions.ROLE_ID,
          referencedColumnName = ID,
          foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT),
          insertable = false,
          updatable = false
        )
      ],
    inverseJoinColumns =
      [
        JoinColumn(
          table = RolePermissions.TABLE_NAME,
          name = RolePermissions.PERMISSIONS_ID,
          referencedColumnName = ID,
          foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT),
          insertable = false,
          updatable = false
        )
      ],
    foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT)
  )
  @Fetch(FetchMode.SUBSELECT)
  @NotFound(action = NotFoundAction.IGNORE)
  var permissions: List<@JvmSuppressWildcards Permissions> = mutableListOf()
}
