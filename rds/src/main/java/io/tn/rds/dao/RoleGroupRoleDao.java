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
 * 角色组  角色
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
@Schema(title = "角色组  角色")
@Table(name = RoleGroupRoleDao.$T_NAME, indexes = {
    @Index(name = "role_group_id_idx", columnList = "role_group_id"),
    @Index(name = "role_id_idx", columnList = "role_id"),
})
public class RoleGroupRoleDao extends BaseDao implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  public static final String $T_NAME = "role_group_role";

  public static final String ROLE_GROUP_ID = "role_group_id";

  public static final String ROLE_ID = "role_id";

  /**
   * 用户组
   */
  @Schema(
      name = ROLE_GROUP_ID,
      description = "用户组"
  )
  @Column(table = $T_NAME,
      name = ROLE_GROUP_ID)
  @Nullable
  private String roleGroupId;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    var that = (RoleGroupRoleDao) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
