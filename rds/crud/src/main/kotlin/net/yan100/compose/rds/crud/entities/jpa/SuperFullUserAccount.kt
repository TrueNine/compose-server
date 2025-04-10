package net.yan100.compose.rds.crud.entities.jpa

import jakarta.persistence.ConstraintMode.NO_CONSTRAINT
import jakarta.persistence.FetchType.EAGER
import jakarta.persistence.ForeignKey
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import net.yan100.compose.consts.IDbNames
import net.yan100.compose.meta.annotations.MetaDef
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode.SUBSELECT
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction.IGNORE

@MetaDef(shadow = true)
interface SuperFullUserAccount : SuperUserAccount {
  /** 角色组 */
  @get:ManyToMany(fetch = EAGER, targetEntity = RoleGroup::class)
  @get:JoinTable(
    name = UserRoleGroup.TABLE_NAME,
    joinColumns =
      [
        JoinColumn(
          name = "user_id",
          referencedColumnName = IDbNames.ID,
          foreignKey = ForeignKey(NO_CONSTRAINT),
          insertable = false,
          updatable = false,
        )
      ],
    inverseJoinColumns =
      [
        JoinColumn(
          name = "role_group_id",
          referencedColumnName = IDbNames.ID,
          foreignKey = ForeignKey(NO_CONSTRAINT),
          insertable = false,
          updatable = false,
        )
      ],
    foreignKey = ForeignKey(NO_CONSTRAINT),
  )
  @get:Fetch(SUBSELECT)
  @get:NotFound(action = IGNORE)
  var roleGroups: MutableList<RoleGroup>
}
