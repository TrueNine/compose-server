package com.truenine.component.rds.entity.supers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.truenine.component.rds.base.BaseEntity;
import com.truenine.component.rds.entity.AttachmentLocationEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NotFound;

import java.io.Serial;
import java.io.Serializable;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;
import static org.hibernate.annotations.NotFoundAction.IGNORE;

@Getter
@Setter
@MappedSuperclass
public class SuperAttachmentEntity extends BaseEntity implements Serializable {
  public static final String TABLE_NAME = "attachment";
  public static final String ATTACHMENT_LOCATION_ID = "attachment_location_id";
  public static final String META_NAME = "meta_name";
  public static final String SAVE_NAME = "save_name";
  public static final String SIZE = "size";
  public static final String MIME_TYPE = "mime_type";
  @Serial
  private static final long serialVersionUID = 1L;

  @Schema(title = "附件地址 id")
  @Column(name = ATTACHMENT_LOCATION_ID, nullable = false)
  private Long attachmentLocationId;

  /**
   * 原始名称
   */
  @Schema(title = "原始名称")
  @Column(name = META_NAME, nullable = false)
  private String metaName;

  /**
   * 存储后名称
   */
  @Nullable
  @JsonIgnore
  @Schema(title = "存储后名称", hidden = true)
  @Column(name = SAVE_NAME)
  private String saveName;

  /**
   * 文件大小
   */
  @Nullable
  @Schema(title = "文件大小")
  @Column(name = SIZE)
  private Long size;

  /**
   * MIME TYPE
   */
  @Nullable
  @Schema(title = "MIME TYPE")
  @Column(name = MIME_TYPE)
  private String mimeType;
}

