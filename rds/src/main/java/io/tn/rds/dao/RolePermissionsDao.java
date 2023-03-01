package io.tn.rds.dao;

import io.tn.rds.base.BaseDao;
import io.tn.rds.base.BaseDao;
import jakarta.persistence.Index;
import org.hibernate.Hibernate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

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
@Table(name = RolePermissionsDao.$T_NAME, indexes = {
    @Index(name = "role_id_idx", columnList = "role_id"),
    @Index(name = "permissions_id_idx", columnList = "permissions_id"),
})
public class RolePermissionsDao extends BaseDao implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  public static final String $T_NAME = "role_permissions";

  public static final String ROLE_ID = "role_id";

  public static final String PERMISSIONS_ID = "permissions_id";

  /**
   * 角色
   */
  @Schema(
      name = ROLE_ID,
      description = "角色"
  )
  @Column(table = $T_NAME,
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
  @Column(table = $T_NAME,
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
