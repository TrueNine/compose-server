package net.yan100.compose.rds.entities

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import net.yan100.compose.rds.core.entities.BaseEntity
import net.yan100.compose.rds.core.entities.TreeEntity
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate


/**
 * # 部门
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "部门")
@Table(name = SuperRoleGroup.TABLE_NAME)
open class Dept : TreeEntity() {
  @Column(name = NAME)
  @Schema(title = "名称")
  open var name: String? = null

  @Column(name = DOC)
  @Schema(title = "描述")
  open var doc: String? = null


  companion object {
    const val TABLE_NAME = "dept"
    const val NAME = "name"
    const val DOC = "doc"
  }
}


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
