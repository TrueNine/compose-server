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
package net.yan100.compose.rds.entities.relationship

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.annotation.Nullable
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import net.yan100.compose.core.alias.RefId
import net.yan100.compose.rds.core.entities.IEntity
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate

/**
 * 用户 角色组
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "用户  角色组")
@Table(name = UserRoleGroup.TABLE_NAME)
class UserRoleGroup : IEntity() {
  /** 用户 */
  @Nullable @Schema(title = "用户") @Column(name = USER_ID) lateinit var userId: RefId

  /** 权限组 */
  @Nullable @Schema(title = "权限组") @Column(name = ROLE_GROUP_ID) lateinit var roleGroupId: RefId

  companion object {
    const val TABLE_NAME: String = "user_role_group"
    const val USER_ID: String = "user_id"
    const val ROLE_GROUP_ID: String = "role_group_id"
  }
}
