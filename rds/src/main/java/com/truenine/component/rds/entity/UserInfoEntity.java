package com.truenine.component.rds.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.truenine.component.core.annotations.SensitiveRef;
import com.truenine.component.core.exceptions.KnownException;
import com.truenine.component.rds.base.BaseEntity;
import com.truenine.component.rds.converters.AesEncryptConverter;
import com.truenine.component.rds.converters.typing.GenderTypingConverter;
import com.truenine.component.rds.typing.GenderTyping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.AUTO;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;
import static org.hibernate.annotations.NotFoundAction.IGNORE;

/**
 * 用户信息
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
@Entity
@Schema(title = "用户信息")
@Table(name = UserInfoEntity.TABLE_NAME)
public class UserInfoEntity extends BaseEntity implements Serializable {

  public static final String TABLE_NAME = "user_info";
  public static final String USER_ID = "user_id";
  public static final String AVATAR_IMG_ID = "avatar_img_id";
  public static final String FIRST_NAME = "first_name";
  public static final String LAST_NAME = "last_name";
  public static final String EMAIL = "email";
  public static final String BIRTHDAY = "birthday";
  public static final String ADDRESS_DETAILS_ID = "address_details_id";
  public static final String PHONE = "phone";
  public static final String ID_CARD = "id_card";
  public static final String GENDER = "gender";
  public static final String WECHAT_OPEN_ID = "wechat_open_id";
  public static final String MAPPED_BY_USER = "user";
  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * 用户
   */
  @Schema(title = "用户")
  @NotNull
  @Column(name = USER_ID, nullable = false)
  private Long userId;

  @OneToOne
  @JoinColumn(
    name = USER_ID,
    referencedColumnName = ID,
    foreignKey = @ForeignKey(NO_CONSTRAINT),
    insertable = false,
    updatable = false
  )
  @JsonBackReference
  @NotFound(action = IGNORE)
  private UserEntity user;

  /**
   * 用户头像
   */
  @Nullable
  @Schema(title = "用户头像")
  @Column(name = AVATAR_IMG_ID)
  private Long avatarImgId;


  @Schema(title = "头像")
  @ManyToOne
  @JoinColumn(
    name = AVATAR_IMG_ID,
    referencedColumnName = ID,
    foreignKey = @ForeignKey(NO_CONSTRAINT),
    insertable = false,
    updatable = false
  )
  @NotFound(action = IGNORE)
  private AttachmentEntity avatarImage;

  /**
   * 姓
   */
  @Nullable
  @Schema(title = "姓")
  @Column(name = FIRST_NAME)
  @Convert(converter = AesEncryptConverter.class)
  private String firstName;

  /**
   * 名
   */
  @Nullable
  @Schema(title = "名")
  @Column(name = LAST_NAME)
  private String lastName;

  @Schema(title = "全名")
  @Transient
  private String fullName;

  /**
   * 邮箱
   */
  @Email
  @Nullable
  @Schema(title = "邮箱")
  @Column(name = EMAIL)
  private String email;
  /**
   * 生日
   */
  @Schema(title = "生日")
  @Column(name = BIRTHDAY)
  @Nullable
  private LocalDate birthday;

  /**
   * 地址
   */
  @Schema(title = "地址", requiredMode = AUTO)
  @Column(name = ADDRESS_DETAILS_ID)
  @Nullable
  private Long addressDetailsId;

  /**
   * 地址
   */
  @Schema(title = "地址", requiredMode = NOT_REQUIRED, accessMode = READ_ONLY)
  @ManyToOne
  @JoinColumn(
    name = ADDRESS_DETAILS_ID,
    referencedColumnName = ID,
    foreignKey = @ForeignKey(NO_CONSTRAINT),
    insertable = false,
    updatable = false
  )
  @NotFound(action = IGNORE)
  private AddressDetailsEntity addressDetails;

  /**
   * 电话号码
   */
  @Nullable
  @Schema(title = "电话号码")
  @Column(name = PHONE, unique = true)
  @SensitiveRef(SensitiveRef.Strategy.PHONE)
  private String phone;
  /**
   * 身份证
   */
  @Schema(title = "身份证")
  @Column(name = ID_CARD, unique = true)
  @Nullable
  @SensitiveRef(SensitiveRef.Strategy.IDCARD)
  private String idCard;

  /**
   * 性别：0女，1难，2未知
   */
  @Nullable
  @Schema(title = " 性别：0女，1难，2未知")
  @Column(name = GENDER)
  @Convert(converter = GenderTypingConverter.class)
  private GenderTyping gender;

  /**
   * 微信个人 openId
   */
  @Nullable
  @Schema(title = "微信个人 openId")
  @Column(name = WECHAT_OPEN_ID)
  private String wechatOpenId;

  @Column(name = ID, insertable = false, updatable = false)
  private Long wechatOauth2Id;

  @Transient
  @Schema(requiredMode = NOT_REQUIRED)
  public String getFullName() {
    return firstName + lastName;
  }

  @Transient
  @Schema(requiredMode = NOT_REQUIRED)
  public void setFullName(String fullName) {
    throw new KnownException("不需要设置参数 fullPath", new IllegalAccessException(), 400);
  }
}
