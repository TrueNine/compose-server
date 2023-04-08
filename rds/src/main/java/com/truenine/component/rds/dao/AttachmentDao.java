package com.truenine.component.rds.dao;

import com.truenine.component.rds.base.BaseDao;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * 文件
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@Getter
@Setter
@ToString
@DynamicInsert
@DynamicUpdate
@Entity
@Schema(title = "附件")
@Table(name = AttachmentDao.TABLE_NAME)
public class AttachmentDao extends BaseDao implements Serializable {

  public static final String TABLE_NAME = "attachment";
  public static final String ATTACHMENT_LOCATION_ID = "attachment_location_id";
  public static final String META_NAME = "meta_name";
  public static final String SAVE_NAME = "save_name";
  public static final String SIZE = "size";
  public static final String MIME_TYPE = "mime_type";
  @Serial
  private static final long serialVersionUID = 1L;
  /**
   * 存储base路径
   */
  @Schema(
    name = ATTACHMENT_LOCATION_ID,
    description = "存储base路径"
  )
  @Column(table = TABLE_NAME,
    name = ATTACHMENT_LOCATION_ID,
    nullable = false)
  private String attachmentLocationId;

  /**
   * 原始名称
   */
  @Schema(
    name = META_NAME,
    description = "原始名称"
  )
  @Column(table = TABLE_NAME,
    name = META_NAME,
    nullable = false)
  private String metaName;

  /**
   * 存储后名称
   */
  @Schema(
    name = SAVE_NAME,
    description = "存储后名称"
  )
  @Column(table = TABLE_NAME,
    name = SAVE_NAME)
  @Nullable
  private String saveName;

  /**
   * 文件大小
   */
  @Schema(
    name = SIZE,
    description = "文件大小"
  )
  @Column(table = TABLE_NAME,
    name = SIZE)
  @Nullable
  private Long size;

  /**
   * MIME TYPE
   */
  @Schema(
    name = MIME_TYPE,
    description = "MIME TYPE"
  )
  @Column(table = TABLE_NAME,
    name = MIME_TYPE)
  @Nullable
  private String mimeType;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    var that = (AttachmentDao) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
