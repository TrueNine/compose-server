package net.yan100.compose.rds.crud.entities.jpa

import jakarta.persistence.*
import net.yan100.compose.core.consts.IDbNames
import net.yan100.compose.meta.annotations.MetaDef
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction

@MetaDef(shadow = true)
interface SuperFullRoleGroup : SuperRoleGroup {
  /** 角色 */
  @get:ManyToMany(fetch = FetchType.EAGER, targetEntity = FullRole::class)
  @get:JoinTable(
    name = RoleGroupRole.TABLE_NAME,
    joinColumns =
      [
        JoinColumn(
          table = RoleGroupRole.TABLE_NAME,
          name = "role_group_id",
          referencedColumnName = IDbNames.ID,
          foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT),
          insertable = false,
          updatable = false,
        )
      ],
    inverseJoinColumns =
      [
        JoinColumn(
          table = RoleGroupRole.TABLE_NAME,
          name = "role_id",
          referencedColumnName = IDbNames.ID,
          foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT),
          insertable = false,
          updatable = false,
        )
      ],
    foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT),
  )
  @get:Fetch(FetchMode.SUBSELECT)
  @get:NotFound(action = NotFoundAction.IGNORE)
  var roles: MutableList<@JvmSuppressWildcards FullRole>
}
