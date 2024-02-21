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
package net.yan100.compose.rds.entities.relationship

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import net.yan100.compose.core.alias.RefId
import net.yan100.compose.rds.core.entities.IEntity
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate

/**
 * 角色组  角色
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "角色组  角色")
@Table(name = RoleGroupRole.TABLE_NAME)
class RoleGroupRole : IEntity() {
  /**
   * 用户组
   */
  @Schema(title = "用户组")
  @Column(name = ROLE_GROUP_ID)
  lateinit var roleGroupId: RefId

  /**
   * 角色
   */
  @Schema(title = "角色")
  @Column(name = ROLE_ID)
  lateinit var roleId: RefId

  companion object {
    const val TABLE_NAME: String = "role_group_role"
    const val ROLE_GROUP_ID: String = "role_group_id"
    const val ROLE_ID: String = "role_id"
  }
}
