package net.yan100.compose.rds.entities

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.annotation.Nullable
import jakarta.persistence.*
import jakarta.persistence.ForeignKey
import jakarta.persistence.Table
import net.yan100.compose.rds.Fk
import net.yan100.compose.rds.core.entities.BaseEntity
import net.yan100.compose.rds.entities.relationship.RoleGroupRole
import org.hibernate.annotations.*

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
  @ManyToMany(fetch = FetchType.EAGER, targetEntity = FullRole::class)
  @JoinTable(
    name = RoleGroupRole.TABLE_NAME,
    joinColumns = [JoinColumn(
      table = RoleGroupRole.TABLE_NAME,
      name = RoleGroupRole.ROLE_GROUP_ID,
      referencedColumnName = ID,
      foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT),
      insertable = false,
      updatable = false
    )],
    inverseJoinColumns = [JoinColumn(
      table = RoleGroupRole.TABLE_NAME,
      name = RoleGroupRole.ROLE_ID,
      referencedColumnName = ID,
      foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT),
      insertable = false,
      updatable = false
    )],
    foreignKey = Fk(ConstraintMode.NO_CONSTRAINT)
  )
  @Fetch(FetchMode.SUBSELECT)
  @NotFound(action = NotFoundAction.IGNORE)
  open var roles: List<FullRole>? = mutableListOf()
}
