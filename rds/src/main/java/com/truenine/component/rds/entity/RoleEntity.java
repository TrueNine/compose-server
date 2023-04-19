package com.truenine.component.rds.entity;

import com.truenine.component.rds.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;
import static org.hibernate.annotations.NotFoundAction.IGNORE;

/**
 * 角色
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
@Entity
@Schema(title = "角色")
@Table(name = RoleEntity.TABLE_NAME)
public class RoleEntity extends BaseEntity implements Serializable {

  public static final String TABLE_NAME = "role";
  public static final String NAME = "name";
  public static final String DOC = "doc";
  @Serial
  private static final long serialVersionUID = 1L;
  /**
   * 角色名称
   */
  @Nullable
  @Schema(title = "角色名称")
  @Column(name = NAME)
  private String name;

  /**
   * 角色描述
   */
  @Nullable
  @Column(name = DOC)
  @Schema(title = "角色描述")
  private String doc;

  /**
   * 权限
   */
  @Schema(title = "权限")
  @ManyToMany(targetEntity = PermissionsEntity.class)
  @JoinTable(
    name = RolePermissionsEntity.TABLE_NAME,
    joinColumns = @JoinColumn(
      table = RolePermissionsEntity.TABLE_NAME,
      name = RolePermissionsEntity.ROLE_ID,
      referencedColumnName = ID,
      foreignKey = @ForeignKey(NO_CONSTRAINT)
    ),
    inverseJoinColumns = @JoinColumn(
      table = RolePermissionsEntity.TABLE_NAME,
      name = RolePermissionsEntity.PERMISSIONS_ID,
      referencedColumnName = ID,
      foreignKey = @ForeignKey(NO_CONSTRAINT)
    ),
    foreignKey = @ForeignKey(NO_CONSTRAINT)
  )
  @NotFound(action = IGNORE)
  private List<PermissionsEntity> permissions;
}
