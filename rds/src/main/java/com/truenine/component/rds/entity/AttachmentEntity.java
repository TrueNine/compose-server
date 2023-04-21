package com.truenine.component.rds.entity;

import com.truenine.component.rds.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;

import java.io.Serial;
import java.io.Serializable;

import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;
import static org.hibernate.annotations.NotFoundAction.IGNORE;

/**
 * 文件
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
@Entity
@Schema(title = "附件")
@Table(name = AttachmentEntity.TABLE_NAME)
public class AttachmentEntity extends BaseEntity implements Serializable {

  public static final String TABLE_NAME = "attachment";
  public static final String ATTACHMENT_LOCATION_ID = "attachment_location_id";
  public static final String META_NAME = "meta_name";
  public static final String SAVE_NAME = "save_name";
  public static final String SIZE = "size";
  public static final String MIME_TYPE = "mime_type";
  @Serial
  private static final long serialVersionUID = 1L;

  @Column(name = ATTACHMENT_LOCATION_ID, nullable = false)
  private Long attachmentLocationId;

  /**
   * URL
   */
  @Nullable
  @Schema(title = "URL")
  @ManyToOne
  @JoinColumn(insertable = false, updatable = false, name = ATTACHMENT_LOCATION_ID, referencedColumnName = ID, foreignKey = @ForeignKey(NO_CONSTRAINT))
  @NotFound(action = IGNORE)
  private AttachmentLocationEntity location;

  /**
   * 原始名称
   */
  @Schema(title = "原始名称")
  @Column(name = META_NAME, nullable = false)
  private String metaName;

  /**
   * 存储后名称
   */
  @Schema(title = "存储后名称")
  @Column(name = SAVE_NAME)
  @Nullable
  private String saveName;

  /**
   * 文件大小
   */
  @Schema(title = "文件大小")
  @Column(name = SIZE)
  @Nullable
  private Long size;

  /**
   * MIME TYPE
   */
  @Schema(title = "MIME TYPE")
  @Column(name = MIME_TYPE)
  @Nullable
  private String mimeType;


  @Nullable
  @Transient
  @Schema(title = "全路径")
  private String fullPath;

  /**
   * @return 全路径
   */
  @Nullable
  @Transient
  public String getFullPath() {
    if (this.location != null) {
      // 切除尾部斜杠
      var link = this.location.getBaseUrl();
      if (!link.endsWith("/")) {
        link += "/";
      }
      return link + this.saveName;
    } else {
      return "/" + this.saveName;
    }
  }

  @Transient
  public void setFullPath(String fullPath) {
  }
}
