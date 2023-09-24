package net.yan100.compose.rds.base

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode
import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Version
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

  /**
   * 乐观锁版本
   */
  @Version
  @JsonIgnore
  @Column(name = RLV, nullable = false, columnDefinition = "INT DEFAULT 0")
  @Schema(hidden = true, title = "乐观锁版本", requiredMode = RequiredMode.NOT_REQUIRED)
  open var rlv: Long? = null

  /**
   * 逻辑删除标志
   */
  @JsonIgnore
  @Column(name = LDF, columnDefinition = "INT DEFAULT 0")
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

  companion object {
    /**
     * 乐观锁版本
     */
    const val RLV = DataBaseBasicFieldNames.LOCK_VERSION

    /**
     * 逻辑删除标志
     */
    const val LDF = DataBaseBasicFieldNames.LOGIC_DELETE_FLAG
  }
}
