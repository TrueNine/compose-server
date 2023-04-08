package com.truenine.component.rds.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.truenine.component.rds.dao.AttachmentDao;
import com.truenine.component.rds.dao.AttachmentLocationDao;
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
@Table(name = AttachmentDao.TABLE_NAME)
@SecondaryTable(
  name = AttachmentLocationDao.TABLE_NAME,
  foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
)
@Schema(title = "附件")
public class AttachmentModel {

  @Id
  @Schema(hidden = true)
  @JsonIgnore
  @Expose(deserialize = false)
  @Column(name = AttachmentDao.ID)
  private String id;

  @Schema(title = "文件名")
  @Column(name = AttachmentDao.META_NAME)
  private String name;

  @Schema(
    title = "文件大小 byte",
    name = "byteSize",
    defaultValue = "0",
    description = "文件大小 byte"
  )
  @Column(name = AttachmentDao.SIZE)
  private Long size;

  @Schema(
    title = "mime-type 类型",
    name = "mimeType",
    nullable = true,
    example = "text/html",
    defaultValue = "application/octet-stream",
    description = "mime-type 类型"
  )
  @Column(name = AttachmentDao.MIME_TYPE)
  private String mimeType;

  @Column(
    table = AttachmentLocationDao.TABLE_NAME,
    name = AttachmentLocationDao.BASE_URL
  )
  @JoinTable(
    name = AttachmentLocationDao.TABLE_NAME,
    foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT),
    joinColumns = {@JoinColumn(
      name = AttachmentLocationDao.ID,
      referencedColumnName = AttachmentDao.ATTACHMENT_LOCATION_ID
    )}
  )
  @Schema(hidden = true)
  @JsonIgnore
  @Expose(deserialize = false)
  @NotFound(action = NotFoundAction.IGNORE)
  private String baseUrl;

  @Column(name = AttachmentDao.SAVE_NAME)
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
