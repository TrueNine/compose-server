package net.yan100.compose.rds.entity

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.annotation.Nullable
import jakarta.persistence.*
import jakarta.persistence.FetchType.EAGER
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction
import java.io.Serial


@MappedSuperclass
open class SuperRoleEntity : net.yan100.compose.rds.base.BaseEntity() {
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

    @Serial
    private const val serialVersionUID = 1L
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
@Table(name = SuperRoleEntity.TABLE_NAME)
open class RoleEntity : SuperRoleEntity()

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = SuperRoleEntity.TABLE_NAME)
open class FullRoleEntity : SuperRoleEntity() {
  /**
   * 权限
   */
  @Schema(title = "权限", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @ManyToMany(fetch = EAGER, targetEntity = PermissionsEntity::class)
  @JoinTable(
    name = net.yan100.compose.rds.entity.relationship.RolePermissionsEntity.TABLE_NAME,
    joinColumns = [JoinColumn(
      table = net.yan100.compose.rds.entity.relationship.RolePermissionsEntity.TABLE_NAME,
      name = net.yan100.compose.rds.entity.relationship.RolePermissionsEntity.ROLE_ID,
      referencedColumnName = ID,
      foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT),
      insertable = false,
      updatable = false
    )],
    inverseJoinColumns = [JoinColumn(
      table = net.yan100.compose.rds.entity.relationship.RolePermissionsEntity.TABLE_NAME,
      name = net.yan100.compose.rds.entity.relationship.RolePermissionsEntity.PERMISSIONS_ID,
      referencedColumnName = ID,
      foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT),
      insertable = false,
      updatable = false
    )],
    foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT)
  )
  @NotFound(action = NotFoundAction.IGNORE)
  open var permissions: List<PermissionsEntity> = listOf()
}


@MappedSuperclass
open class SuperRoleGroupEntity : net.yan100.compose.rds.base.BaseEntity() {
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

    @Serial
    private const val serialVersionUID = 1L
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
@Table(name = SuperRoleGroupEntity.TABLE_NAME)
open class RoleGroupEntity : SuperRoleGroupEntity()


@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = SuperRoleGroupEntity.TABLE_NAME)
open class AllRoleGroupEntity : SuperRoleGroupEntity() {
  /**
   * 角色
   */
  @Schema(title = "角色", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @ManyToMany(fetch = EAGER, targetEntity = FullRoleEntity::class)
  @JoinTable(
    name = net.yan100.compose.rds.entity.relationship.RoleGroupRoleEntity.TABLE_NAME,
    joinColumns = [JoinColumn(
      table = net.yan100.compose.rds.entity.relationship.RoleGroupRoleEntity.TABLE_NAME,
      name = net.yan100.compose.rds.entity.relationship.RoleGroupRoleEntity.ROLE_GROUP_ID,
      referencedColumnName = ID,
      foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT),
      insertable = false,
      updatable = false
    )],
    inverseJoinColumns = [JoinColumn(
      table = net.yan100.compose.rds.entity.relationship.RoleGroupRoleEntity.TABLE_NAME,
      name = net.yan100.compose.rds.entity.relationship.RoleGroupRoleEntity.ROLE_ID,
      referencedColumnName = ID,
      foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT),
      insertable = false,
      updatable = false
    )],
    foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT)
  )
  @NotFound(action = NotFoundAction.IGNORE)
  open var roles: List<FullRoleEntity> = listOf()
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
@Table(name = PermissionsEntity.TABLE_NAME)
open class PermissionsEntity : net.yan100.compose.rds.base.BaseEntity() {
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

    @Serial
    private const val serialVersionUID = 1L
  }
}
