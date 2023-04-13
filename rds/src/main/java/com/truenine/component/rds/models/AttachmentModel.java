package com.truenine.component.rds.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.truenine.component.rds.entity.AttachmentEntity;
import com.truenine.component.rds.entity.AttachmentLocationEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * 文件出参
 *
 * @author TrueNine
 * @since 2023-01-01
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = AttachmentEntity.TABLE_NAME)
@SecondaryTable(
  name = AttachmentLocationEntity.TABLE_NAME,
  foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
)
@Schema(title = "附件")
public class AttachmentModel {

  @Id
  @Schema(hidden = true)
  @JsonIgnore
  @Expose(deserialize = false)
  @Column(name = AttachmentEntity.ID)
  private Long id;

  @Schema(title = "文件名")
  @Column(name = AttachmentEntity.META_NAME)
  private String name;

  @Schema(
    title = "文件大小 byte",
    name = "byteSize",
    defaultValue = "0",
    description = "文件大小 byte"
  )
  @Column(name = AttachmentEntity.SIZE)
  private Long size;

  @Schema(
    title = "mime-type 类型",
    name = "mimeType",
    nullable = true,
    example = "text/html",
    defaultValue = "application/octet-stream",
    description = "mime-type 类型"
  )
  @Column(name = AttachmentEntity.MIME_TYPE)
  private String mimeType;

  @Column(
    table = AttachmentLocationEntity.TABLE_NAME,
    name = AttachmentLocationEntity.BASE_URL
  )
  @JoinTable(
    name = AttachmentLocationEntity.TABLE_NAME,
    foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT),
    joinColumns = {@JoinColumn(
      name = AttachmentLocationEntity.ID,
      referencedColumnName = AttachmentEntity.ATTACHMENT_LOCATION_ID
    )}
  )
  @Schema(hidden = true)
  @JsonIgnore
  @Expose(deserialize = false)
  @NotFound(action = NotFoundAction.IGNORE)
  private String baseUrl;

  @Column(name = AttachmentEntity.SAVE_NAME)
  @NotFound(action = NotFoundAction.IGNORE)
  @JsonIgnore
  @Expose(deserialize = false)
  private String saveName;


  @Transient
  @Schema(
    title = "fullUrl",
    description = "访问的全路径")
  public String getFullUrl() {
    return this.baseUrl + "/" + this.saveName;
  }
}
