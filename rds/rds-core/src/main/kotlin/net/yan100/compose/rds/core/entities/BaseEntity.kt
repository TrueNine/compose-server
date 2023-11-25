package net.yan100.compose.rds.core.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode
import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Version
import net.yan100.compose.core.alias.BigSerial
import net.yan100.compose.core.consts.DataBaseBasicFieldNames

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
open class BaseEntity : AnyEntity() {
  companion object {
    const val RLV = DataBaseBasicFieldNames.LOCK_VERSION
    const val LDF = DataBaseBasicFieldNames.LOGIC_DELETE_FLAG
  }

  /**
   * 乐观锁版本
   */
  @Version
  @JsonIgnore
  @Column(name = RLV)
  @Schema(hidden = true, title = "乐观锁版本", requiredMode = RequiredMode.NOT_REQUIRED)
  open var rlv: BigSerial? = null

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
  open var ldf: Boolean? = null

  override fun asNew() {
    super.asNew()
    ldf = false
    rlv = null
  }

  override fun toString(): String {
    return withToString(
      super.toString(),
      "ldf" to ldf,
      "rlv" to rlv
    )
  }
}
