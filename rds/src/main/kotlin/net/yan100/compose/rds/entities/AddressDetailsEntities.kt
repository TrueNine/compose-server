package net.yan100.compose.rds.entities

import com.fasterxml.jackson.annotation.JsonBackReference
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.annotation.Nullable
import jakarta.persistence.*
import jakarta.persistence.ForeignKey
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import net.yan100.compose.core.alias.RefId
import net.yan100.compose.core.alias.SerialCode
import net.yan100.compose.core.annotations.NonDesensitizedRef
import net.yan100.compose.core.consts.Regexes
import net.yan100.compose.core.lang.WGS84
import net.yan100.compose.rds.converters.WGS84Converter
import net.yan100.compose.rds.core.entities.IEntity
import org.hibernate.annotations.*


@MappedSuperclass
abstract class SuperAddressDetails : IEntity() {
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

    /**
     * 地址 id
     */
    @Schema(title = "地址 id")
    @Column(name = ADDRESS_ID, nullable = false)
    var addressId: String? = null

    /**
     * 联系电话
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = Regexes.CHINA_PHONE, message = "请输入正确的电话号码")
    @Schema(title = "联系电话")
    @Column(name = PHONE)
    var phone: String? = null

    /**
     * ## 用户 id
     */
    @NotBlank(message = "用户 id 不能数为空")
    @Schema(title = "用户 id")
    @Column(name = USER_ID)
    lateinit var userId: RefId

    /**
     * 联系人名称
     */
    @NotBlank(message = "请留一个姓名")
    @Schema(title = "联系人名称")
    @Column(name = NAME)
    var name: String? = null

    /**
     * 地址代码
     */
    @NotBlank(message = "地址代码不能为空")
    @Schema(title = "地址代码")
    @Column(name = ADDRESS_CODE)
    lateinit var addressCode: SerialCode

    /**
     * 地址详情
     */
    @NotBlank(message = "详细地址不能为空")
    @Schema(title = "地址详情")
    @Column(name = ADDRESS_DETAILS, nullable = false)
    lateinit var addressDetails: String

    /**
     * 定位
     */
    @Nullable
    @Schema(title = "定位")
    @Column(name = CENTER)
    @Convert(converter = WGS84Converter::class)
    var center: WGS84? = null
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
    override lateinit var addressDetails: String

    /**
     * 地址代码
     */
    @get:NonDesensitizedRef
    @NotBlank(message = "地址代码不能为空")
    @Schema(title = "地址代码")
    @Column(name = ADDRESS_CODE)
    override lateinit var addressCode: SerialCode

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
    @ManyToOne(fetch = FetchType.EAGER)
    @Schema(title = "地址", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JoinColumn(
        name = ADDRESS_ID,
        referencedColumnName = ID,
        foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT),
        insertable = false,
        updatable = false
    )
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonBackReference
    @Fetch(FetchMode.JOIN)
    var address: Address? = null
}
