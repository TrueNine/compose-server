package net.yan100.compose.rds.crud.entities.jpa

import jakarta.persistence.*
import net.yan100.compose.consts.IDbNames
import net.yan100.compose.meta.annotations.MetaDef
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction

@MetaDef(shadow = true)
interface SuperFullAddressDetails : SuperAddressDetails {
  /** 地址 */
  @get:ManyToOne(fetch = FetchType.EAGER)
  @get:JoinColumn(
    name = "address_id",
    referencedColumnName = IDbNames.ID,
    foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT),
    insertable = false,
    updatable = false,
  )
  @get:NotFound(action = NotFoundAction.IGNORE)
  @get:Fetch(FetchMode.JOIN)
  var address: Address?
}
