package net.yan100.compose.rds.entities

import com.fasterxml.jackson.annotation.JsonBackReference
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import net.yan100.compose.meta.annotations.MetaDef
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction

/** 完全的用户信息 */
@MetaDef(shadow = true)
@MappedSuperclass
abstract class SuperFullUserInfo : SuperUserInfo() {
  /** 连接的用户 */
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id", referencedColumnName = ID, foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable = false, updatable = false)
  @JsonBackReference
  @NotFound(action = NotFoundAction.IGNORE)
  open var usr: Usr? = null

  /** 用户住址 */
  @Schema(title = "用户住址", requiredMode = Schema.RequiredMode.NOT_REQUIRED, accessMode = Schema.AccessMode.READ_ONLY)
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(
    name = "address_details_id",
    referencedColumnName = ID,
    foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT),
    insertable = false,
    updatable = false,
  )
  @NotFound(action = NotFoundAction.IGNORE)
  open var addressDetails: AddressDetails? = null

  /** 用户头像 */
  @Schema(title = "头像")
  @ManyToOne(targetEntity = LinkedAttachment::class)
  @JoinColumn(name = "avatar_img_id", referencedColumnName = ID, foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable = false, updatable = false)
  @NotFound(action = NotFoundAction.IGNORE)
  open var avatarImage: LinkedAttachment? = null
}
