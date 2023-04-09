package com.truenine.component.rds.entity;

import com.truenine.component.rds.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
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
 * api
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
@Schema(title = "api")
@Table(name = ApiEntity.TABLE_NAME, indexes = {
  @Index(name = "permissions_id_idx", columnList = "permissions_id"),
})
public class ApiEntity extends BaseEntity implements Serializable {

  public static final String TABLE_NAME = "api";
  public static final String NAME = "name";
  public static final String DOC = "doc";
  public static final String PERMISSIONS_ID = "permissions_id";
  public static final String API_PATH = "api_path";
  public static final String API_METHOD = "api_method";
  public static final String API_PROTOCOL = "api_protocol";
  @Serial
  private static final long serialVersionUID = 1L;
  /**
   * 名称
   */
  @Schema(
    name = NAME,
    description = "名称"
  )
  @Column(table = TABLE_NAME,
    name = NAME)
  @Nullable
  private String name;

  /**
   * 描述
   */
  @Schema(
    name = DOC,
    description = "描述"
  )
  @Column(table = TABLE_NAME,
    name = DOC)
  @Nullable
  private String doc;

  /**
   * 访问需要权限
   */
  @Schema(
    name = PERMISSIONS_ID,
    description = "访问需要权限"
  )
  @Column(table = TABLE_NAME,
    name = PERMISSIONS_ID)
  @Nullable
  private Long permissionsId;

  /**
   * 路径
   */
  @Schema(
    name = API_PATH,
    description = "路径"
  )
  @Column(table = TABLE_NAME,
    name = API_PATH)
  @Nullable
  private String apiPath;

  /**
   * 请求方式
   */
  @Schema(
    name = API_METHOD,
    description = "请求方式"
  )
  @Column(table = TABLE_NAME,
    name = API_METHOD)
  @Nullable
  private String apiMethod;

  /**
   * 请求协议
   */
  @Schema(
    name = API_PROTOCOL,
    description = "请求协议"
  )
  @Column(table = TABLE_NAME,
    name = API_PROTOCOL)
  @Nullable
  private String apiProtocol;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    var that = (ApiEntity) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
