package net.yan100.compose.rds.crud.entities.jpa

import jakarta.persistence.ConstraintMode.NO_CONSTRAINT
import jakarta.persistence.FetchType.EAGER
import jakarta.persistence.ForeignKey
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import net.yan100.compose.consts.IDbNames
import net.yan100.compose.meta.annotations.MetaDef
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode.SUBSELECT

@MetaDef(shadow = true)
interface SuperFullAddress : SuperAddress {
  /** 当前地址包含的地址详情 */
  @get:OneToMany(targetEntity = AddressDetails::class, fetch = EAGER)
  @get:JoinColumn(
    name = "address_id",
    referencedColumnName = IDbNames.ID,
    foreignKey = ForeignKey(NO_CONSTRAINT),
    insertable = false,
    updatable = false,
  )
  @get:Fetch(SUBSELECT)
  var details: List<AddressDetails>
}
