package com.truenine.component.rds.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.truenine.component.rds.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serial;
import java.io.Serializable;

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
@Table(name = AttachmentLocationEntity.TABLE_NAME)
public class AttachmentLocationEntity extends BaseEntity implements Serializable {

  public static final String TABLE_NAME = "attachment_location";
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
  @Column(table = TABLE_NAME,
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
  @Column(table = TABLE_NAME,
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
  @Column(table = TABLE_NAME,
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
  @Column(table = TABLE_NAME,
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
}
