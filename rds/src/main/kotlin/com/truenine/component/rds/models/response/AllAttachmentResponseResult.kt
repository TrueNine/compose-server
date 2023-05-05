package com.truenine.component.rds.models.response

import com.truenine.component.rds.entity.AttachmentEntity
import com.truenine.component.rds.entity.AttachmentLocationEntity
import com.truenine.component.rds.entity.supers.SuperAttachmentEntity
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED
import jakarta.annotation.Nullable
import jakarta.persistence.*
import jakarta.persistence.ConstraintMode.NO_CONSTRAINT
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction.IGNORE

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = AttachmentEntity.TABLE_NAME)
data class AllAttachmentResponseResult(
  /**
   * URL
   */
  @Nullable
  @ManyToOne
  @Schema(title = "URL", requiredMode = NOT_REQUIRED)
  @JoinColumn(
    name = ATTACHMENT_LOCATION_ID,
    referencedColumnName = ID,
    foreignKey = ForeignKey(NO_CONSTRAINT),
    insertable = false,
    updatable = false
  )
  @NotFound(action = IGNORE)
  var location: AttachmentLocationEntity? = null
) : SuperAttachmentEntity() {
  @get:Transient
  @get:Schema(title = "全路径")
  var fullPath: String
    /**
     * @return 全路径
     */
    get() = location?.let { "${it.baseUrl}/${saveName}" } ?: "/$saveName"
    set(_) {}
}
