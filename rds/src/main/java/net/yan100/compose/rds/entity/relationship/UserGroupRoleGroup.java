package net.yan100.compose.rds.entity.relationship;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.yan100.compose.rds.base.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

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
@Table(name = UserGroupRoleGroup.TABLE_NAME)
public class UserGroupRoleGroup extends BaseEntity {

  public static final String TABLE_NAME = "user_group_role_group";
  public static final String ROLE_GROUP_ID = "role_group_id";
  public static final String USER_GROUP_ID = "user_group_id";

  /**
   * 角色组
   */
  @Nullable
  @Schema(title = "角色组")
  @Column(name = ROLE_GROUP_ID)
  private String roleGroupId;

  /**
   * 用户组
   */
  @Nullable
  @Schema(title = "用户组")
  @Column(name = USER_GROUP_ID)
  private String userGroupId;
}
