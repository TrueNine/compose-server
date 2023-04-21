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
 * 角色组  角色
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@Getter
@Setter
@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "角色组  角色")
@Table(name = RoleGroupRoleEntity.TABLE_NAME)
public class RoleGroupRoleEntity extends BaseEntity implements Serializable {
  public static final String TABLE_NAME = "role_group_role";
  public static final String ROLE_GROUP_ID = "role_group_id";
  public static final String ROLE_ID = "role_id";
  @Serial
  private static final long serialVersionUID = 1L;
  /**
   * 用户组
   */
  @Nullable
  @Schema(title = "用户组")
  @Column(name = ROLE_GROUP_ID)
  private Long roleGroupId;

  /**
   * 角色
   */
  @Nullable
  @Schema(title = "角色")
  @Column(name = ROLE_ID)
  private Long roleId;
}
