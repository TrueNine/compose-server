package io.tn.rds.dao;

import io.swagger.v3.oas.annotations.media.Schema;
import io.tn.rds.base.BaseDao;
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
@Schema(title = "文件")
@Table(name = FileDao.$T_NAME)
public class FileDao extends BaseDao implements Serializable {

  public static final String $T_NAME = "file";
  public static final String FILE_LOCATION_ID = "file_location_id";
  public static final String META_NAME = "meta_name";
  public static final String SAVE_NAME = "save_name";
  public static final String BYTE_SIZE = "byte_size";
  public static final String DESCRIPTOR = "descriptor";
  public static final String MIME_TYPE = "mime_type";
  @Serial
  private static final long serialVersionUID = 1L;
  /**
   * 存储base路径
   */
  @Schema(
    name = FILE_LOCATION_ID,
    description = "存储base路径"
  )
  @Column(table = $T_NAME,
    name = FILE_LOCATION_ID,
    nullable = false)
  private String fileLocationId;

  /**
   * 原始名称
   */
  @Schema(
    name = META_NAME,
    description = "原始名称"
  )
  @Column(table = $T_NAME,
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
  @Column(table = $T_NAME,
    name = SAVE_NAME)
  @Nullable
  private String saveName;

  /**
   * 文件大小
   */
  @Schema(
    name = BYTE_SIZE,
    description = "文件大小"
  )
  @Column(table = $T_NAME,
    name = BYTE_SIZE)
  @Nullable
  private Long byteSize;

  /**
   * 文件描述符
   */
  @Schema(
    name = DESCRIPTOR,
    description = "文件描述符"
  )
  @Column(table = $T_NAME,
    name = DESCRIPTOR)
  @Nullable
  private String descriptor;

  /**
   * MIME TYPE
   */
  @Schema(
    name = MIME_TYPE,
    description = "MIME TYPE"
  )
  @Column(table = $T_NAME,
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
    var that = (FileDao) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
