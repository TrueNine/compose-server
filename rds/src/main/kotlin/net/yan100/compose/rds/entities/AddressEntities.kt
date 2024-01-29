package net.yan100.compose.rds.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED
import jakarta.annotation.Nullable
import jakarta.persistence.*
import jakarta.persistence.ConstraintMode.NO_CONSTRAINT
import jakarta.persistence.FetchType.EAGER
import net.yan100.compose.core.alias.SerialCode
import net.yan100.compose.core.lang.WGS84
import net.yan100.compose.rds.converters.WGS84Converter
import net.yan100.compose.rds.core.entities.TreeEntity
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode.SUBSELECT

@MappedSuperclass
abstract class SuperAddress : TreeEntity() {
  companion object {
    const val LEAF = "leaf"
    const val TABLE_NAME = "address"
    const val CODE = "code"
    const val NAME = "name"
    const val YEAR_VERSION = "year_version"
    const val LEVEL = "level"
    const val CENTER = "center"
  }

  /**
   * 代码
   */
  @Nullable
  @Schema(title = "代码")
  @Column(name = CODE)
  lateinit var code: SerialCode

  /**
   * 名称
   */
  @Nullable
  @Schema(title = "名称")
  @Column(name = NAME)
  lateinit var name: String

  /**
   * 级别 0 为国家
   */
  @Nullable
  @Schema(title = "级别 0 为国家")
  @Column(name = LEVEL)
  var level: Int? = null

  /**
   * 年份版本号
   */
  @JsonIgnore
  @Schema(name = "年份版本号")
  @Column(name = YEAR_VERSION)
  lateinit var yearVersion: String

  /**
   * 定位
   */
  @Nullable
  @Schema(title = "定位")
  @Column(name = CENTER)
  @Convert(converter = WGS84Converter::class)
  var center: WGS84? = null

  /**
   * 是否为终结地址（如市辖区）
   */
  @Schema(title = "是否为终结地址（如市辖区）")
  @Column(name = LEAF)
  var leaf: Boolean = false
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
class Address : SuperAddress()

@Entity
@DynamicInsert
@DynamicUpdate
@Schema(title = "行政区代码")
@Table(name = SuperAddress.TABLE_NAME)
class FullAddress : SuperAddress() {
  /**
   * 当前地址包含的地址详情
   */
  @Schema(title = "包含的地址详情", requiredMode = NOT_REQUIRED)
  @OneToMany(targetEntity = AddressDetails::class, fetch = EAGER)
  @JoinColumn(
    name = SuperAddressDetails.Companion.ADDRESS_ID,
    referencedColumnName = ID,
    foreignKey = ForeignKey(NO_CONSTRAINT),
    insertable = false,
    updatable = false
  )
  @Fetch(SUBSELECT)
  var details: List<AddressDetails> = listOf()
}
