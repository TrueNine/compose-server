package net.yan100.compose.rds.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED
import jakarta.annotation.Nullable
import jakarta.persistence.*
import jakarta.persistence.ConstraintMode.NO_CONSTRAINT
import jakarta.persistence.FetchType.EAGER
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import net.yan100.compose.core.annotations.NonDesensitizedRef
import net.yan100.compose.core.annotations.SensitiveRef
import net.yan100.compose.core.annotations.Strategy
import net.yan100.compose.core.consts.Regexes
import net.yan100.compose.core.lang.WGS84
import net.yan100.compose.rds.base.BaseEntity
import net.yan100.compose.rds.base.TreeEntity
import net.yan100.compose.rds.converters.WGS84Converter
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode.JOIN
import org.hibernate.annotations.FetchMode.SUBSELECT
import org.hibernate.annotations.NotFound
import org.hibernate.annotations.NotFoundAction.IGNORE

@MappedSuperclass
open class SuperAddressDetails : BaseEntity() {
  /**
   * 地址 id
   */
  @Schema(title = "地址 id")
  @Column(name = ADDRESS_ID, nullable = false)
  open var addressId: String? = null

  /**
   * 联系电话
   */
  @NotBlank(message = "手机号不能为空")
  @Pattern(regexp = Regexes.CHINA_PHONE, message = "请输入正确的电话号码")
  @get:SensitiveRef(Strategy.PHONE)
  @Schema(title = "联系电话")
  @Column(name = PHONE)
  open var phone: String? = null

  /**
   * ## 用户 id
   */
  @NotBlank(message = "用户 id 不能数为空")
  @Schema(title = "用户 id")
  @Column(name = USER_ID)
  open var userId: String? = null

  /**
   * 联系人名称
   */
  @NotBlank(message = "请留一个姓名")
  @get:SensitiveRef(Strategy.NAME)
  @Schema(title = "联系人名称")
  @Column(name = NAME)
  open var name: String? = null

  /**
   * 地址代码
   */
  @NotBlank(message = "地址代码不能为空")
  @Schema(title = "地址代码")
  @get:SensitiveRef(Strategy.PASSWORD)
  @Column(name = ADDRESS_CODE)
  open var addressCode: String? = null

  /**
   * 地址详情
   */
  @NotBlank(message = "详细地址不能为空")
  @get:SensitiveRef(Strategy.ADDRESS)
  @Schema(title = "地址详情")
  @Column(name = ADDRESS_DETAILS, nullable = false)
  open var addressDetails: String? = null

  /**
   * 定位
   */
  @Nullable
  @Schema(title = "定位")
  @Column(name = CENTER)
  @Convert(converter = WGS84Converter::class)
  open var center: WGS84? = null

  companion object {
    const val PHONE = "phone"
    const val NAME = "name"
    const val USER_ID = "user_id"
    const val ADDRESS_CODE = "address_code"
    const val TABLE_NAME = "address_details"
    const val ADDRESS_ID = "address_id"
    const val ADDRESS_DETAILS = "address_details"
    const val CENTER = "center"
  }
}

/**
 * 详细地址
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "详细地址")
@Table(name = SuperAddressDetails.TABLE_NAME)
class AddressDetails : SuperAddressDetails()


@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "非脱敏详细地址")
@Table(name = SuperAddressDetails.TABLE_NAME)
class NonDesensitizedAddressDetails : SuperAddressDetails() {
  /**
   * 联系电话
   */
  @get:NonDesensitizedRef
  @NotBlank(message = "手机号不能为空")
  @Pattern(regexp = Regexes.CHINA_PHONE, message = "请输入正确的电话号码")
  @Schema(title = "联系电话")
  @Column(name = PHONE)
  override var phone: String? = null

  /**
   * 地址详情
   */
  @get:NonDesensitizedRef
  @NotBlank(message = "详细地址不能为空")
  @Schema(title = "地址详情")
  @Column(name = ADDRESS_DETAILS, nullable = false)
  override var addressDetails: String? = null

  /**
   * 地址代码
   */
  @get:NonDesensitizedRef
  @NotBlank(message = "地址代码不能为空")
  @Schema(title = "地址代码")
  @Column(name = ADDRESS_CODE)
  override var addressCode: String? = null


  /**
   * 联系人名称
   */
  @get:NonDesensitizedRef
  @NotBlank(message = "请留一个姓名")
  @Schema(title = "联系人名称")
  @Column(name = NAME)
  override var name: String? = null
}


@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "详细地址")
@Table(name = SuperAddressDetails.TABLE_NAME)
class FullAddressDetails : SuperAddressDetails() {
  /**
   * 地址
   */
  @ManyToOne(fetch = EAGER)
  @Schema(title = "地址", requiredMode = NOT_REQUIRED)
  @JoinColumn(
    name = ADDRESS_ID,
    referencedColumnName = ID,
    foreignKey = ForeignKey(NO_CONSTRAINT),
    insertable = false,
    updatable = false
  )
  @NotFound(action = IGNORE)
  @JsonBackReference
  @Fetch(JOIN)
  var address: Address? = null
}

@MappedSuperclass
open class SuperAddress : TreeEntity() {
  /**
   * 代码
   */
  @Nullable
  @Schema(title = "代码")
  @Column(name = CODE)
  open var code: String? = null

  /**
   * 名称
   */
  @Nullable
  @Schema(title = "名称")
  @Column(name = NAME)
  open var name: String? = null

  /**
   * 级别 0 为国家
   */
  @Nullable
  @Schema(title = "级别 0 为国家")
  @Column(name = LEVEL)
  open var level: Int? = null

  /**
   * 定位
   */
  @Nullable
  @Schema(title = "定位")
  @Column(name = CENTER)
  @Convert(converter = WGS84Converter::class)
  open var center: WGS84? = null

  /**
   * 是否为终结地址（如市辖区）
   */
  @Schema(title = "是否为终结地址（如市辖区）")
  @Column(name = LEAF)
  open var leaf: Boolean = false

  companion object {
    const val LEAF = "leaf"
    const val TABLE_NAME = "address"
    const val CODE = "code"
    const val NAME = "name"
    const val LEVEL = "level"
    const val CENTER = "center"
  }
}

/**
 * 行政区代码
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "行政区代码")
@Table(name = SuperAddress.TABLE_NAME)
open class Address : SuperAddress()

@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "行政区代码")
@Table(name = SuperAddress.TABLE_NAME)
open class FullAddress : SuperAddress() {
  /**
   * 当前地址包含的地址详情
   */
  @Schema(title = "包含的地址详情", requiredMode = NOT_REQUIRED)
  @OneToMany(targetEntity = AddressDetails::class, fetch = EAGER)
  @JoinColumn(
    name = SuperAddressDetails.ADDRESS_ID,
    referencedColumnName = ID,
    foreignKey = ForeignKey(NO_CONSTRAINT),
    insertable = false,
    updatable = false
  )
  @Fetch(SUBSELECT)
  open var details: List<AddressDetails> = listOf()
}
