package net.yan100.compose.rds.entity

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.annotation.Nullable
import jakarta.persistence.*
import jakarta.persistence.ConstraintMode.NO_CONSTRAINT
import jakarta.persistence.FetchType.EAGER
import net.yan100.compose.rds.base.BaseEntity
import net.yan100.compose.rds.entity.relationship.RoleGroupRole
import net.yan100.compose.rds.entity.relationship.RoleGroupRole.ROLE_GROUP_ID
import net.yan100.compose.rds.entity.relationship.RoleGroupRole.ROLE_ID
import net.yan100.compose.rds.entity.relationship.RolePermissions
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode.SUBSELECT
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction.IGNORE


@MappedSuperclass
open class SuperRole : BaseEntity() {
  /**
   * 角色名称
   */
  @Nullable
  @Schema(title = "角色名称")
  @Column(name = NAME)
  open var name: String? = null

  /**
   * 角色描述
   */
  @Nullable
  @Column(name = DOC)
  @Schema(title = "角色描述")
  open var doc: String? = null

  companion object {
    const val TABLE_NAME = "role"
    const val NAME = "name"
    const val DOC = "doc"
  }
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
open class Role : SuperRole()

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = SuperRole.TABLE_NAME)
open class FullRole : SuperRole() {
  /**
   * 权限
   */
  @Schema(title = "权限", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @ManyToMany(fetch = EAGER, targetEntity = Permissions::class)
  @JoinTable(
    name = RolePermissions.TABLE_NAME,
    joinColumns = [JoinColumn(
      table = RolePermissions.TABLE_NAME,
      name = RolePermissions.ROLE_ID,
      referencedColumnName = ID,
      foreignKey = ForeignKey(NO_CONSTRAINT),
      insertable = false,
      updatable = false
    )],
    inverseJoinColumns = [JoinColumn(
      table = RolePermissions.TABLE_NAME,
      name = RolePermissions.PERMISSIONS_ID,
      referencedColumnName = ID,
      foreignKey = ForeignKey(NO_CONSTRAINT),
      insertable = false,
      updatable = false
    )],
    foreignKey = ForeignKey(NO_CONSTRAINT)
  )
  @Fetch(SUBSELECT)
  @NotFound(action = IGNORE)
  open var permissions: List<Permissions> = listOf()
}


@MappedSuperclass
open class SuperRoleGroup : BaseEntity() {
  /**
   * 名称
   */
  @Schema(title = "名称")
  @Column(name = NAME)
  @Nullable
  open var name: String? = null

  /**
   * 描述
   */
  @Nullable
  @Schema(title = "描述")
  @Column(name = DOC)
  open var doc: String? = null

  companion object {
    const val TABLE_NAME = "role_group"
    const val NAME = "name"
    const val DOC = "doc"
  }
}

/**
 * 角色组
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "角色组")
@Table(name = SuperRoleGroup.TABLE_NAME)
open class RoleGroup : SuperRoleGroup()


@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = SuperRoleGroup.TABLE_NAME)
open class FullRoleGroup : SuperRoleGroup() {
  /**
   * 角色
   */
  @Schema(title = "角色", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @ManyToMany(fetch = EAGER, targetEntity = FullRole::class)
  @JoinTable(
    name = RoleGroupRole.TABLE_NAME,
    joinColumns = [JoinColumn(
      table = RoleGroupRole.TABLE_NAME,
      name = ROLE_GROUP_ID,
      referencedColumnName = ID,
      foreignKey = ForeignKey(NO_CONSTRAINT),
      insertable = false,
      updatable = false
    )],
    inverseJoinColumns = [JoinColumn(
      table = RoleGroupRole.TABLE_NAME,
      name = ROLE_ID,
      referencedColumnName = ID,
      foreignKey = ForeignKey(NO_CONSTRAINT),
      insertable = false,
      updatable = false
    )],
    foreignKey = ForeignKey(NO_CONSTRAINT)
  )
  @Fetch(SUBSELECT)
  @NotFound(action = IGNORE)
  open var roles: List<FullRole> = listOf()
}


/**
 * # 部门
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "部门")
@Table(name = SuperRoleGroup.TABLE_NAME)
open class Dept : BaseEntity() {
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
 * 权限
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "权限")
@Table(name = Permissions.TABLE_NAME)
open class Permissions : BaseEntity() {
  /**
   * 权限名
   */
  @Nullable
  @Schema(title = "权限名")
  @Column(name = NAME)
  open var name: String? = null

  /**
   * 权限描述
   */
  @Nullable
  @Schema(title = "权限描述")
  @Column(name = DOC)
  open var doc: String? = null

  companion object {
    const val TABLE_NAME = "permissions"
    const val NAME = "name"
    const val DOC = "doc"
  }
}
