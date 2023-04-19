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
 * 角色组
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
@Entity
@Schema(title = "角色组")
@Table(name = RoleGroupEntity.TABLE_NAME)
public class RoleGroupEntity extends BaseEntity implements Serializable {

  public static final String TABLE_NAME = "role_group";
  public static final String NAME = "name";
  public static final String DOC = "doc";
  @Serial
  private static final long serialVersionUID = 1L;
  /**
   * 名称
   */
  @Schema(title = "名称")
  @Column(name = NAME)
  @Nullable
  private String name;

  /**
   * 描述
   */
  @Nullable
  @Schema(title = "描述")
  @Column(name = DOC)
  private String doc;

  /**
   * 角色
   */
  @Schema(title = "角色")
  @ManyToMany(targetEntity = RoleEntity.class)
  @JoinTable(
    name = RoleGroupRoleEntity.TABLE_NAME,
    joinColumns = @JoinColumn(
      table = RoleGroupRoleEntity.TABLE_NAME,
      name = RoleGroupRoleEntity.ROLE_GROUP_ID,
      referencedColumnName = ID,
      foreignKey = @ForeignKey(NO_CONSTRAINT),
      insertable = false,
      updatable = false
    ),
    inverseJoinColumns = @JoinColumn(
      table = RoleGroupRoleEntity.TABLE_NAME,
      name = RoleGroupRoleEntity.ROLE_ID,
      referencedColumnName = ID,
      foreignKey = @ForeignKey(NO_CONSTRAINT),
      insertable = false,
      updatable = false
    ),
    foreignKey = @ForeignKey(NO_CONSTRAINT)
  )
  @NotFound(action = IGNORE)
  private List<RoleEntity> roles;
}
