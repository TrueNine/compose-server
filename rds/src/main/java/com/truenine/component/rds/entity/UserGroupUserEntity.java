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
@DynamicInsert
@DynamicUpdate
@Entity
@Schema(title = "用户组 用户")
@Table(name = UserGroupUserEntity.TABLE_NAME)
public class UserGroupUserEntity extends BaseEntity implements Serializable {

  public static final String TABLE_NAME = "user_group_user";
  public static final String USER_GROUP_ID = "user_group_id";
  public static final String USER_ID = "user_id";
  @Serial
  private static final long serialVersionUID = 1L;
  /**
   * 用户组
   */
  @Schema(title = "用户组")
  @Column(name = USER_GROUP_ID, nullable = false)
  private Long userGroupId;

  /**
   * 用户
   */
  @Schema(title= "用户")
  @Column(name = USER_ID, nullable = false)
  private Long userId;
}
