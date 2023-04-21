package com.truenine.component.rds.entity;

import com.truenine.component.rds.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
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
  @Schema(title = "基本url")
  @Column(name = BASE_URL, nullable = false)
  private String baseUrl;

  /**
   * 资源路径名称
   */
  @Schema(title = "资源路径名称")
  @Column(name = NAME, nullable = false)
  private String name;

  /**
   * 资源路径描述
   */
  @Schema(title = "资源路径描述")
  @Column(name = DOC)
  @Nullable
  private String doc;

  /**
   * 存储类别
   */
  @Schema(title = "存储类别")
  @Column(name = TYPE, nullable = false)
  private String type = "R";

  @Transient
  @Schema(title = "是否为远程存储")
  private Boolean rn;

  @Transient
  public void setRn(Boolean storageRnType) {
    this.type = storageRnType ? "R" : "N";
  }


  @Nullable
  @Transient
  public Boolean isRn() {
    return "R".equals(type);
  }
}
