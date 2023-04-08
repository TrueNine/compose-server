package com.truenine.component.rds.dao;

import com.truenine.component.rds.base.BaseDao;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

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
@Table(name = UserDao.TABLE_NAME, indexes = {
  @Index(name = "account_idx", columnList = "account"),
})
public class UserDao extends BaseDao implements Serializable {

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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    var that = (UserDao) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
