package com.truenine.component.rds.entity;

import com.truenine.component.rds.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;
import static org.hibernate.annotations.NotFoundAction.IGNORE;

/**
 * 用户
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
@Schema(title = "用户")
@Table(name = UserEntity.TABLE_NAME)
public class UserEntity extends BaseEntity implements Serializable {

  public static final String TABLE_NAME = "user";
  public static final String ACCOUNT = "account";
  public static final String NICK_NAME = "nick_name";
  public static final String DOC = "doc";
  public static final String PWD_ENC = "pwd_enc";
  public static final String BAN_TIME = "ban_time";
  public static final String LAST_LOGIN_TIME = "last_login_time";
  @Serial
  private static final long serialVersionUID = 1L;
  /**
   * 账号
   */
  @Schema(
    name = ACCOUNT,
    description = "账号"
  )
  @Column(table = TABLE_NAME,
    name = ACCOUNT,
    nullable = false,
    unique = true)
  private String account;

  /**
   * 呢称
   */
  @Schema(
    name = NICK_NAME,
    description = "呢称"
  )
  @Column(table = TABLE_NAME,
    name = NICK_NAME)
  @Nullable
  private String nickName;

  /**
   * 描述
   */
  @Schema(
    name = DOC,
    description = "描述"
  )
  @Column(table = TABLE_NAME,
    name = DOC)
  @Nullable
  private String doc;

  /**
   * 密码
   */
  @Schema(
    name = PWD_ENC,
    description = "密码"
  )
  @Column(table = TABLE_NAME,
    name = PWD_ENC)
  @Nullable
  private String pwdEnc;

  /**
   * 被封禁结束时间
   */
  @Schema(
    name = BAN_TIME,
    description = "被封禁结束时间"
  )
  @Column(table = TABLE_NAME,
    name = BAN_TIME)
  @Nullable
  private LocalDateTime banTime;

  /**
   * 最后请求时间
   */
  @Schema(
    name = LAST_LOGIN_TIME,
    description = "最后请求时间"
  )
  @Column(table = TABLE_NAME,
    name = LAST_LOGIN_TIME)
  @Nullable
  private LocalDateTime lastLoginTime;

  /**
   * 用户信息
   */
  @Schema(title = "用户信息")
  @OneToOne
  @JoinColumn(
    name = ID,
    referencedColumnName = UserInfoEntity.USER_ID,
    foreignKey = @ForeignKey(NO_CONSTRAINT)
  )
  @NotFound(action = IGNORE)
  private UserInfoEntity info;

  /**
   * 角色组
   */
  @Schema(title = "角色组")
  @ManyToMany(targetEntity = RoleGroupEntity.class)
  @JoinTable(
    name = UserRoleGroupEntity.TABLE_NAME,
    joinColumns = @JoinColumn(
      name = UserRoleGroupEntity.USER_ID,
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
