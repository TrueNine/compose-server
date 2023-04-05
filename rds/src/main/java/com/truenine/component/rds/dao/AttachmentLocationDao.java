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
@Schema(title = "附件地址")
@Table(name = AttachmentLocationDao.$T_NAME)
public class AttachmentLocationDao extends BaseDao implements Serializable {

  public static final String $T_NAME = "attachment_location";
  public static final String BASE_URL = "base_url";
  public static final String NAME = "name";
  public static final String DOC = "doc";
  public static final String TYPE = "type";
  @Serial
  private static final long serialVersionUID = 1L;
  /**
   * 基本url
   */
  @Schema(
    name = BASE_URL,
    description = "基本url"
  )
  @Column(table = $T_NAME,
    name = BASE_URL,
    nullable = false)
  private String baseUrl;

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
    name = TYPE,
    description = "存储类别"
  )
  @Column(table = $T_NAME,
    name = TYPE,
    nullable = false)
  private String type;

  public void rn(Boolean storageRnType) {
    this.type = storageRnType ? "R" : "N";
  }

  @Nullable
  public Boolean rn() {
    return "R".equals(type);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    var that = (AttachmentLocationDao) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
