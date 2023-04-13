package com.truenine.component.rds.entity;

import com.truenine.component.rds.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Table(name = UserGroupUserEntity.TABLE_NAME, indexes = {
  @Index(name = "user_group_id_idx", columnList = "user_group_id"),
  @Index(name = "user_id_idx", columnList = "user_id"),
})
public class UserGroupUserEntity extends BaseEntity implements Serializable {

  public static final String TABLE_NAME = "user_group_user";
  public static final String USER_GROUP_ID = "user_group_id";
  public static final String USER_ID = "user_id";
  @Serial
  private static final long serialVersionUID = 1L;
  /**
   * 用户组
   */
  @Schema(
    name = USER_GROUP_ID,
    description = "用户组"
  )
  @Column(table = TABLE_NAME,
    name = USER_GROUP_ID,
    nullable = false)
  private Long userGroupId;

  /**
   * 用户
   */
  @Schema(
    name = USER_ID,
    description = "用户"
  )
  @Column(table = TABLE_NAME,
    name = USER_ID,
    nullable = false)
  private Long userId;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    var that = (UserGroupUserEntity) o;
    return id != null && Objects.equals(id, that.id);
  }
}
