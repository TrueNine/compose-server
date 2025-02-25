package net.yan100.compose.rds.crud.entities.jpa

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.*
import net.yan100.compose.core.consts.IDbNames
import net.yan100.compose.meta.annotations.MetaDef
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction

/** 完全的用户信息 */
@MetaDef(shadow = true)
interface SuperFullUserInfo : SuperUserInfo {
  /** 连接的用户 */
  @get:OneToOne(fetch = FetchType.EAGER)
  @get:JoinColumn(
    name = "user_id",
    referencedColumnName = IDbNames.ID,
    foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT),
    insertable = false,
    updatable = false,
  )
  @get:JsonBackReference
  @get:NotFound(action = NotFoundAction.IGNORE)
  var usr: UserAccount?

  /** 用户住址 */
  @get:ManyToOne(fetch = FetchType.EAGER)
  @get:JoinColumn(
    name = "address_details_id",
    referencedColumnName = IDbNames.ID,
    foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT),
    insertable = false,
    updatable = false,
  )
  @get:NotFound(action = NotFoundAction.IGNORE)
  var addressDetails: AddressDetails?

  /** 用户头像 */
  @get:ManyToOne(targetEntity = LinkedAttachment::class)
  @get:JoinColumn(
    name = "avatar_img_id",
    referencedColumnName = IDbNames.ID,
    foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT),
    insertable = false,
    updatable = false,
  )
  @get:NotFound(action = NotFoundAction.IGNORE)
  var avatarImage: LinkedAttachment?
}
