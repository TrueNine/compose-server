package net.yan100.compose.rds.entities.relationship;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.yan100.compose.rds.core.entities.IEntity;
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
@DynamicInsert
@DynamicUpdate
@Entity
@Schema(title = "用户  角色组")
@Table(name = UserRoleGroup.TABLE_NAME)
public class UserRoleGroup extends IEntity {
  public static final String TABLE_NAME = "user_role_group";
  public static final String USER_ID = "user_id";
  public static final String ROLE_GROUP_ID = "role_group_id";

  /**
   * 用户
   */
  @Nullable
  @Schema(title = "用户")
  @Column(name = USER_ID)
  private String userId;

  /**
   * 权限组
   */
  @Nullable
  @Schema(title = "权限组")
  @Column(name = ROLE_GROUP_ID)
  private String roleGroupId;
}
