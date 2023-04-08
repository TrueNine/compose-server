package com.truenine.component.rds.dao;

import com.truenine.component.rds.base.BaseDao;
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
 * 角色  权限
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
@Schema(title = "角色  权限")
@Table(name = RolePermissionsDao.TABLE_NAME, indexes = {
  @Index(name = "role_id_idx", columnList = "role_id"),
  @Index(name = "permissions_id_idx", columnList = "permissions_id"),
})
public class RolePermissionsDao extends BaseDao implements Serializable {

  public static final String TABLE_NAME = "role_permissions";
  public static final String ROLE_ID = "role_id";
  public static final String PERMISSIONS_ID = "permissions_id";
  @Serial
  private static final long serialVersionUID = 1L;
  /**
   * 角色
   */
  @Schema(
    name = ROLE_ID,
    description = "角色"
  )
  @Column(table = TABLE_NAME,
    name = ROLE_ID)
  @Nullable
  private String roleId;

  /**
   * 权限
   */
  @Schema(
    name = PERMISSIONS_ID,
    description = "权限"
  )
  @Column(table = TABLE_NAME,
    name = PERMISSIONS_ID)
  @Nullable
  private String permissionsId;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    var that = (RolePermissionsDao) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
