package net.yan100.compose.rds.entities.relationship;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.yan100.compose.rds.core.entities.BaseEntity;
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
@DynamicInsert
@DynamicUpdate
@Entity
@Schema(title = "角色  权限")
@Table(name = RolePermissions.TABLE_NAME)
public class RolePermissions extends BaseEntity {
  public static final String TABLE_NAME = "role_permissions";
  public static final String ROLE_ID = "role_id";
  public static final String PERMISSIONS_ID = "permissions_id";

  /**
   * 角色
   */
  @Nullable
  @Schema(title = "角色")
  @Column(name = ROLE_ID)
  private String roleId;

  /**
   * 权限
   */
  @Nullable
  @Schema(title = "权限")
  @Column(name = PERMISSIONS_ID)
  private String permissionsId;
}