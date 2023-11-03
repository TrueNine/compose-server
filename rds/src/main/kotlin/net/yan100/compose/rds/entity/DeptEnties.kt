package net.yan100.compose.rds.entity

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import net.yan100.compose.rds.core.entity.BaseEntity
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate

/**
 * # 用户  部门
 * @author TureNine
 * @since 2023-07-16
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "用户  部门")
@Table(name = UserDept.TABLE_NAME)
open class UserDept : BaseEntity() {
  @Schema(title = "用户id")
  @Column(name = USER_ID)
  open var userId: String? = null

  @Schema(title = "部门id")
  @Column(name = DEPT_ID)
  open var deptId: String? = null

  companion object {
    const val TABLE_NAME = "user_dept"
    const val USER_ID = "user_id"
    const val DEPT_ID = "dept_id"
  }
}
