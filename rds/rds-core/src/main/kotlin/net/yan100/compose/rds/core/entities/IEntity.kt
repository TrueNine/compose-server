package net.yan100.compose.rds.core.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode
import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Transient
import jakarta.persistence.Version
import net.yan100.compose.core.alias.BigSerial
import net.yan100.compose.core.alias.bool
import net.yan100.compose.core.alias.datetime
import net.yan100.compose.core.consts.DataBaseBasicFieldNames
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate

/**
 * jpa顶级抽象类
 *
 *
 * \@CreateBy
 * \@LastModifyBy
 *
 * @author TrueNine
 * @since 2022-12-12
 */
@MappedSuperclass
@Schema(title = "顶级抽象类")
abstract class IEntity : AnyEntity() {
  companion object {
    const val RLV = DataBaseBasicFieldNames.LOCK_VERSION
    const val LDF = DataBaseBasicFieldNames.LOGIC_DELETE_FLAG
    const val CRD = DataBaseBasicFieldNames.CREATE_ROW_DATETIME
    const val MRD = DataBaseBasicFieldNames.MODIFY_ROW_DATETIME
  }

  /**
   * 乐观锁版本
   */
  @Version
  @JsonIgnore
  @Column(name = RLV)
  @Schema(hidden = true, title = "乐观锁版本", requiredMode = RequiredMode.NOT_REQUIRED)
  var rlv: BigSerial? = null
  val databaseTableRowFieldLockVersion: BigSerial?
    @Schema(title = "字段乐观锁版本号")
    @Transient
    @JsonIgnore
    get() = rlv

  @CreatedDate
  @JsonIgnore
  @Schema(title = "表行创建时间")
  @Column(name = CRD)
  var crd: datetime? = null
  val databaseTableRowFieldCreatedDatetime: datetime?
    @Schema(title = "字段创建时间")
    @Transient
    @JsonIgnore
    get() = crd


  @JsonIgnore
  @LastModifiedDate
  @Schema(title = "表行修改时间")
  @Column(name = MRD)
  var mrd: datetime? = null
  val databaseTableRowFieldLastModifyDatetime: datetime?
    @Schema(title = "字段的修改时间")
    @Transient
    @JsonIgnore
    get() = mrd


  /**
   * 逻辑删除标志
   */
  @JsonIgnore
  @Column(name = LDF)
  @Schema(
    hidden = true,
    title = "逻辑删除标志",
    requiredMode = RequiredMode.NOT_REQUIRED,
    accessMode = Schema.AccessMode.READ_ONLY
  )
  var ldf: Boolean? = null
  val databaseTableRowFieldLogicDeleteFlag: bool
    @Schema(title = "是否已经删除")
    @Transient
    @JsonIgnore
    get() = ldf == true

  override fun asNew() {
    super.asNew()
    ldf = false
    rlv = null
  }

  override fun toString(): String {
    return withToString(
      super.toString(),
      LDF to ldf,
      RLV to rlv,
      CRD to crd,
      MRD to mrd
    )
  }
}
