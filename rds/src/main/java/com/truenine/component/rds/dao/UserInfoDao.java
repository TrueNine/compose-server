package com.truenine.component.rds.dao;

import com.truenine.component.rds.base.BaseDao;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * 用户信息
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
@Schema(title = "用户信息")
@Table(name = UserInfoDao.$T_NAME, indexes = {
  @Index(name = "phone_idx", columnList = "phone"),
  @Index(name = "id_card_idx", columnList = "id_card"),
  @Index(name = "user_id_idx", columnList = "user_id"),
  @Index(name = "address_details_id_idx", columnList = "address_details_id"),
  @Index(name = "avatar_img_id_idx", columnList = "avatar_img_id"),
})
public class UserInfoDao extends BaseDao implements Serializable {

  public static final String $T_NAME = "user_info";
  public static final String USER_ID = "user_id";
  public static final String AVATAR_IMG_ID = "avatar_img_id";
  public static final String FIRST_NAME = "first_name";
  public static final String LAST_NAME = "last_name";
  public static final String EMAIL = "email";
  public static final String BIRTHDAY = "birthday";
  public static final String ADDRESS_DETAILS_ID = "address_details_id";
  public static final String PHONE = "phone";
  public static final String ID_CARD = "id_card";
  public static final String SEX = "sex";
  @Serial
  private static final long serialVersionUID = 1L;
  /**
   * 用户
   */
  @Schema(
    name = USER_ID,
    description = "用户"
  )
  @NotBlank
  @Column(table = $T_NAME,
    name = USER_ID,
    nullable = false)
  private String userId;

  /**
   * 用户头像
   */
  @Schema(
    name = AVATAR_IMG_ID,
    description = "用户头像"
  )
  @Column(table = $T_NAME,
    name = AVATAR_IMG_ID)
  @Nullable
  private Long avatarImgId;

  /**
   * 姓
   */
  @NotBlank
  @Schema(
    name = FIRST_NAME,
    description = "姓"
  )
  @Column(table = $T_NAME,
    name = FIRST_NAME)
  @Nullable
  private String firstName;

  /**
   * 名
   */
  @NotBlank
  @Schema(
    name = LAST_NAME,
    description = "名"
  )
  @Column(table = $T_NAME,
    name = LAST_NAME)
  @Nullable
  private String lastName;

  /**
   * 邮箱
   */
  @Email
  @NotBlank
  @Schema(
    name = EMAIL,
    description = "邮箱"
  )
  @Column(table = $T_NAME,
    name = EMAIL)
  @Nullable
  private String email;

  /**
   * 生日
   */
  @Schema(
    name = BIRTHDAY,
    description = "生日"
  )
  @Column(table = $T_NAME,
    name = BIRTHDAY)
  @Nullable
  private LocalDate birthday;

  /**
   * 地址
   */
  @Schema(
    name = ADDRESS_DETAILS_ID,
    description = "地址"
  )
  @Column(table = $T_NAME,
    name = ADDRESS_DETAILS_ID)
  @Nullable
  private Long addressDetailsId;

  /**
   * 电话号码
   */
  @Schema(
    name = PHONE,
    description = "电话号码"
  )
  @Column(table = $T_NAME,
    name = PHONE,
    unique = true)
  @Nullable
  private String phone;

  /**
   * 身份证
   */
  @Schema(
    name = ID_CARD,
    description = "身份证"
  )
  @Column(table = $T_NAME,
    name = ID_CARD,
    unique = true)
  @Nullable
  private String idCard;

  /**
   * 性别：0女，1难，2未知
   */
  @Schema(
    name = SEX,
    description = " 性别：0女，1难，2未知"
  )
  @Column(table = $T_NAME,
    name = SEX)
  @Nullable
  private Byte sex;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    var that = (UserInfoDao) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
