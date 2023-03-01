package io.tn.rds.dao;

import io.tn.core.lang.Str;
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
 * 用户组 用户
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
@Schema(title = "用户组 用户")
@Table(name = UserGroupUserDao.$T_NAME, indexes = {
    @Index(name = "user_group_id_idx", columnList = "user_group_id"),
    @Index(name = "user_id_idx", columnList = "user_id"),
})
public class UserGroupUserDao extends BaseDao implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  public static final String $T_NAME = "user_group_user";

  public static final String USER_GROUP_ID = "user_group_id";

  public static final String USER_ID = "user_id";

  /**
   * 用户组
   */
  @Schema(
      name = USER_GROUP_ID,
      description = "用户组"
  )
  @Column(table = $T_NAME,
      name = USER_GROUP_ID,
      nullable = false)
  private String userGroupId;

  /**
   * 用户
   */
  @Schema(
      name = USER_ID,
      description = "用户"
  )
  @Column(table = $T_NAME,
      name = USER_ID,
      nullable = false)
  private String userId;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    var that = (UserGroupUserDao) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
