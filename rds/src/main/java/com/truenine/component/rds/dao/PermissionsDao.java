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
 * 权限
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
@Schema(title = "权限")
@Table(name = PermissionsDao.$T_NAME)
public class PermissionsDao extends BaseDao implements Serializable {

  public static final String $T_NAME = "permissions";
  public static final String NAME = "name";
  public static final String DOC = "doc";
  @Serial
  private static final long serialVersionUID = 1L;
  /**
   * 权限名
   */
  @Schema(
    name = NAME,
    description = "权限名"
  )
  @Column(table = $T_NAME,
    name = NAME)
  @Nullable
  private String name;

  /**
   * 权限描述
   */
  @Schema(
    name = DOC,
    description = "权限描述"
  )
  @Column(table = $T_NAME,
    name = DOC)
  @Nullable
  private String doc;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    var that = (PermissionsDao) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
