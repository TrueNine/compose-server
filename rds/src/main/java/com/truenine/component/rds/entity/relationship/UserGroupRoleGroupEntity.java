package com.truenine.component.rds.entity.relationship;

import com.truenine.component.rds.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户组  角色组
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
@Entity
@Schema(title = "用户组  角色组")
@Table(name = UserGroupRoleGroupEntity.TABLE_NAME)
public class UserGroupRoleGroupEntity extends BaseEntity implements Serializable {

  public static final String TABLE_NAME = "user_group_role_group";
  public static final String ROLE_GROUP_ID = "role_group_id";
  public static final String USER_GROUP_ID = "user_group_id";
  @Serial
  private static final long serialVersionUID = 1L;
  /**
   * 角色组
   */
  @Nullable
  @Schema(title = "角色组")
  @Column(name = ROLE_GROUP_ID)
  private Long roleGroupId;

  /**
   * 用户组
   */
  @Nullable
  @Schema(title = "用户组")
  @Column(name = USER_GROUP_ID)
  private Long userGroupId;
}
