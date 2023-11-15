package net.yan100.compose.rds.entities

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED
import jakarta.annotation.Nullable
import jakarta.persistence.*
import jakarta.persistence.ConstraintMode.NO_CONSTRAINT
import jakarta.persistence.FetchType.EAGER
import net.yan100.compose.core.lang.WGS84
import net.yan100.compose.rds.converters.WGS84Converter
import net.yan100.compose.rds.core.entities.TreeEntity
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode.SUBSELECT

@MappedSuperclass
open class SuperAddress : TreeEntity() {
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
   * 年份版本号
   */
  @Nullable
  @Schema(name = "年份版本号")
  @Column(name = YEAR_VERSION)
  open var yearVersion: String? = null

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
    name = SuperAddressDetails.Companion.ADDRESS_ID,
    referencedColumnName = ID,
    foreignKey = ForeignKey(NO_CONSTRAINT),
    insertable = false,
    updatable = false
  )
  @Fetch(SUBSELECT)
  open var details: List<AddressDetails> = listOf()
}
