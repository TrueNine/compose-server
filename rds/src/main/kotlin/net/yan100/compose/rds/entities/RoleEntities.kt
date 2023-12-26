package net.yan100.compose.rds.entities

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.annotation.Nullable
import jakarta.persistence.*
import jakarta.persistence.ForeignKey
import jakarta.persistence.Table
import net.yan100.compose.rds.core.entities.BaseEntity
import net.yan100.compose.rds.entities.relationship.RolePermissions
import org.hibernate.annotations.*

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
  @ManyToMany(fetch = FetchType.EAGER, targetEntity = Permissions::class)
  @JoinTable(
    name = RolePermissions.TABLE_NAME,
    joinColumns = [JoinColumn(
      table = RolePermissions.TABLE_NAME,
      name = RolePermissions.ROLE_ID,
      referencedColumnName = ID,
      foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT),
      insertable = false,
      updatable = false
    )],
    inverseJoinColumns = [JoinColumn(
      table = RolePermissions.TABLE_NAME,
      name = RolePermissions.PERMISSIONS_ID,
      referencedColumnName = ID,
      foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT),
      insertable = false,
      updatable = false
    )],
    foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT)
  )
  @Fetch(FetchMode.SUBSELECT)
  @NotFound(action = NotFoundAction.IGNORE)
  open var permissions: List<Permissions>? = null
}
