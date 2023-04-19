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
  @Schema(title = "账号")
  @Column(name = ACCOUNT, nullable = false, unique = true)
  private String account;

  /**
   * 呢称
   */
  @Nullable
  @Schema(title = "呢称")
  @Column(name = NICK_NAME)
  private String nickName;

  /**
   * 描述
   */
  @Nullable
  @Schema(title = "描述")
  @Column(name = DOC)
  private String doc;

  /**
   * 密码
   */
  @Nullable
  @Schema(title = "密码")
  @Column(name = PWD_ENC)
  private String pwdEnc;

  /**
   * 被封禁结束时间
   */
  @Nullable
  @Schema(title = "被封禁结束时间")
  @Column(name = BAN_TIME)
  private LocalDateTime banTime;

  /**
   * 最后请求时间
   */
  @Nullable
  @Schema(title = "最后请求时间")
  @Column(name = LAST_LOGIN_TIME)
  private LocalDateTime lastLoginTime;

  /**
   * 用户信息
   */
  @Schema(title = "用户信息")
  @OneToOne
  @JoinColumn(
    name = ID,
    referencedColumnName = UserInfoEntity.USER_ID,
    foreignKey = @ForeignKey(NO_CONSTRAINT),
    insertable = false,
    updatable = false
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

  /**
   * @return 当前用户是否被封禁
   */
  @Transient
  public boolean isBand() {
    return null != banTime
      && LocalDateTime.now().isBefore(banTime);
  }
}
