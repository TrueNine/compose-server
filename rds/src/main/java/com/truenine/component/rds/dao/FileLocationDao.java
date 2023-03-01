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
 * 文件地址
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
@Schema(title = "文件地址")
@Table(name = FileLocationDao.$T_NAME)
public class FileLocationDao extends BaseDao implements Serializable {

  public static final String $T_NAME = "file_location";
  public static final String URL = "url";
  public static final String NAME = "name";
  public static final String DOC = "doc";
  public static final String STORAGE_RN_TYPE = "storage_rn_type";
  @Serial
  private static final long serialVersionUID = 1L;
  /**
   * 基本url
   */
  @Schema(
    name = URL,
    description = "基本url"
  )
  @Column(table = $T_NAME,
    name = URL,
    nullable = false)
  private String url;

  /**
   * 资源路径名称
   */
  @Schema(
    name = NAME,
    description = "资源路径名称"
  )
  @Column(table = $T_NAME,
    name = NAME,
    nullable = false)
  private String name;

  /**
   * 资源路径描述
   */
  @Schema(
    name = DOC,
    description = "资源路径描述"
  )
  @Column(table = $T_NAME,
    name = DOC)
  @Nullable
  private String doc;

  /**
   * 存储类别
   */
  @Schema(
    name = STORAGE_RN_TYPE,
    description = "存储类别"
  )
  @Column(table = $T_NAME,
    name = STORAGE_RN_TYPE,
    nullable = false)
  private String storageRnType;

  public void rn(Boolean storageRnType) {
    this.storageRnType = storageRnType ? "R" : "N";
  }

  @Nullable
  public Boolean rn() {
    return "R".equals(storageRnType);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    var that = (FileLocationDao) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
