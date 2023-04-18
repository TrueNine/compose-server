package com.truenine.component.rds.entity;

import com.truenine.component.core.annotations.SensitiveRef;
import com.truenine.component.rds.base.BaseEntity;
import com.truenine.component.rds.converters.AesEncryptConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;

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
  @Serial
  private static final long serialVersionUID = 1L;
  /**
   * 用户
   */
  @Schema(
    name = USER_ID,
    description = "用户"
  )
  @NotNull
  @Column(table = TABLE_NAME,
    name = USER_ID,
    nullable = false)
  private Long userId;

  /**
   * 用户头像
   */
  @Schema(
    name = AVATAR_IMG_ID,
    description = "用户头像"
  )
  @Column(table = TABLE_NAME,
    name = AVATAR_IMG_ID)
  @Nullable
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
  @NotFound(action = NotFoundAction.IGNORE)
  private AttachmentEntity avatarImage;

  /**
   * 姓
   */
  @NotBlank
  @Schema(
    name = FIRST_NAME,
    description = "姓"
  )
  @Column(table = TABLE_NAME,
    name = FIRST_NAME)
  @Nullable
  @Convert(converter = AesEncryptConverter.class)
  private String firstName;

  /**
   * 名
   */
  @NotBlank
  @Schema(
    name = LAST_NAME,
    description = "名"
  )
  @Column(table = TABLE_NAME,
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
  @Column(table = TABLE_NAME,
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
  @Column(table = TABLE_NAME,
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
  @Column(table = TABLE_NAME,
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
  @Column(table = TABLE_NAME,
    name = PHONE,
    unique = true)
  @Nullable
  @SensitiveRef(SensitiveRef.Strategy.PHONE)
  private String phone;

  /**
   * 身份证
   */
  @Schema(
    name = ID_CARD,
    description = "身份证"
  )
  @Column(table = TABLE_NAME,
    name = ID_CARD,
    unique = true)
  @Nullable
  @SensitiveRef(SensitiveRef.Strategy.IDCARD)
  private String idCard;

  /**
   * 性别：0女，1难，2未知
   */
  @Schema(
    name = GENDER,
    description = " 性别：0女，1难，2未知"
  )
  @Column(table = TABLE_NAME,
    name = GENDER)
  @Nullable
  private Byte gender;
}
