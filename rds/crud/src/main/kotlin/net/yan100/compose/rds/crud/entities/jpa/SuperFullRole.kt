package net.yan100.compose.rds.crud.entities.jpa

import jakarta.persistence.*
import net.yan100.compose.consts.IDbNames
import net.yan100.compose.meta.annotations.MetaDef
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction

@MetaDef(shadow = true)
interface SuperFullRole : SuperRole {

  /** 权限 */
  @get:ManyToMany(fetch = FetchType.EAGER, targetEntity = Permissions::class)
  @get:JoinTable(
    name = RolePermissions.TABLE_NAME,
    joinColumns =
      [
        JoinColumn(
          table = RolePermissions.TABLE_NAME,
          name = "role_id",
          referencedColumnName = IDbNames.ID,
          foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT),
          insertable = false,
          updatable = false,
        )
      ],
    inverseJoinColumns =
      [
        JoinColumn(
          table = RolePermissions.TABLE_NAME,
          name = "permissions_id",
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
  var permissions: MutableList<@JvmSuppressWildcards Permissions>
}
