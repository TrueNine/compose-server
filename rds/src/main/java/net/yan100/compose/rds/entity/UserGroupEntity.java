package net.yan100.compose.rds.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.yan100.compose.rds.base.BaseEntity;
import net.yan100.compose.rds.entity.relationship.UserGroupRoleGroupEntity;
import net.yan100.compose.rds.entity.relationship.UserGroupUserEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;
import static org.hibernate.annotations.NotFoundAction.IGNORE;

/**
 * 用户组
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@Getter
@Setter
@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "用户组")
@Table(name = UserGroupEntity.TABLE_NAME)
public class UserGroupEntity extends BaseEntity implements Serializable {

  public static final String TABLE_NAME = "user_group";
  public static final String USER_ID = "user_id";
  public static final String NAME = "name";
  public static final String DOC = "doc";
  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * 名称
   */
  @Nullable
  @Schema(title = "名称")
  @Column(name = NAME)
  private String name;

  /**
   * 描述
   */
  @Nullable
  @Schema(title = "描述")
  @Column(name = DOC)
  private String doc;

  /**
   * 创建人
   */
  @Nullable
  @Schema(title = "创建人")
  @Column(name = USER_ID)
  private String userId;

  /**
   * leaderUser
   */
  @Schema(title = "用户组创建人", requiredMode = NOT_REQUIRED)
  @ManyToOne
  @JoinColumn(
    name = USER_ID,
    referencedColumnName = ID,
    foreignKey = @ForeignKey(NO_CONSTRAINT),
    insertable = false,
    updatable = false
  )
  @NotFound(action = IGNORE)
  private UserEntity leader;

  /**
   * 用户组内的用户
   */
  @Schema(title = "用户组内的用户", requiredMode = NOT_REQUIRED)
  @ManyToMany(targetEntity = UserEntity.class)
  @JoinTable(
    name = UserGroupUserEntity.TABLE_NAME,
    joinColumns = @JoinColumn(
      name = UserGroupUserEntity.USER_GROUP_ID,
      referencedColumnName = ID,
      foreignKey = @ForeignKey(NO_CONSTRAINT),
      insertable = false,
      updatable = false
    ),
    inverseJoinColumns = @JoinColumn(
      name = UserGroupUserEntity.USER_ID,
      referencedColumnName = ID,
      foreignKey = @ForeignKey(NO_CONSTRAINT),
      insertable = false,
      updatable = false
    ),
    foreignKey = @ForeignKey(NO_CONSTRAINT)
  )
  @NotFound(action = IGNORE)
  private List<UserEntity> users;

  /**
   * 角色组
   */
  @Schema(title = "角色组", requiredMode = NOT_REQUIRED)
  @ManyToMany(targetEntity = RoleGroupEntity.class)
  @JoinTable(
    name = UserGroupRoleGroupEntity.TABLE_NAME,
    joinColumns = @JoinColumn(
      name = UserGroupRoleGroupEntity.USER_GROUP_ID,
      referencedColumnName = ID,
      foreignKey = @ForeignKey(NO_CONSTRAINT),
      insertable = false,
      updatable = false
    ),
    inverseJoinColumns = @JoinColumn(
      name = UserGroupRoleGroupEntity.ROLE_GROUP_ID,
      referencedColumnName = ID,
      foreignKey = @ForeignKey(NO_CONSTRAINT),
      insertable = false,
      updatable = false
    ),
    foreignKey = @ForeignKey(NO_CONSTRAINT)
  )
  @NotFound(action = IGNORE)
  private List<RoleGroupEntity> roleGroups;
}