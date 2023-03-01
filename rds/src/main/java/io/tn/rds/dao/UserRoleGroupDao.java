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
 * 用户  角色组
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
@Schema(title = "用户  角色组")
@Table(name = UserRoleGroupDao.$T_NAME, indexes = {
    @Index(name = "user_id_idx", columnList = "user_id"),
    @Index(name = "role_group_id_idx", columnList = "role_group_id"),
})
public class UserRoleGroupDao extends BaseDao implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  public static final String $T_NAME = "user_role_group";

  public static final String USER_ID = "user_id";

  public static final String ROLE_GROUP_ID = "role_group_id";

  /**
   * 用户
   */
  @Schema(
      name = USER_ID,
      description = "用户"
  )
  @Column(table = $T_NAME,
      name = USER_ID)
  @Nullable
  private String userId;

  /**
   * 权限组
   */
  @Schema(
      name = ROLE_GROUP_ID,
      description = "权限组"
  )
  @Column(table = $T_NAME,
      name = ROLE_GROUP_ID)
  @Nullable
  private String roleGroupId;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    var that = (UserRoleGroupDao) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
