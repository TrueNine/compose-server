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
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import net.yan100.compose.core.alias.RefId
import net.yan100.compose.rds.core.entities.IEntity
import net.yan100.compose.rds.core.entities.TreeEntity
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate

/** # 部门 */
@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "部门")
@Table(name = SuperRoleGroup.TABLE_NAME)
class Dept : TreeEntity() {
  companion object {
    const val TABLE_NAME = "dept"

    const val NAME = "name"
    const val DOC = "doc"
  }

  @Column(name = NAME) @Schema(title = "名称") lateinit var name: String

  @Column(name = DOC) @Schema(title = "描述") var doc: String? = null
}

/**
 * # 用户 部门
 *
 * @author TureNine
 * @since 2023-07-16
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "用户  部门")
@Table(name = UserDept.TABLE_NAME)
class UserDept : IEntity() {
  companion object {
    const val TABLE_NAME = "user_dept"

    const val USER_ID = "user_id"
    const val DEPT_ID = "dept_id"
  }

  @Schema(title = "用户id") @Column(name = USER_ID) lateinit var userId: RefId

  @Schema(title = "部门id") @Column(name = DEPT_ID) lateinit var deptId: RefId
}
