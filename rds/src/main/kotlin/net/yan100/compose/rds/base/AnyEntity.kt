package net.yan100.compose.rds.base

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import net.yan100.compose.core.consts.DataBaseBasicFieldNames
import net.yan100.compose.core.lang.nonText
import net.yan100.compose.rds.listener.BizCodeInsertListener
import net.yan100.compose.rds.listener.PreSaveDeleteReferenceListener
import net.yan100.compose.rds.listener.SnowflakeIdInsertListener
import net.yan100.compose.rds.listener.TableRowDeletePersistenceListener
import org.hibernate.Hibernate
import org.springframework.data.domain.Persistable
import java.io.Serial
import java.io.Serializable

/**
 * JPA的最基础基类，包括一个 id
 *
 * @author TrueNine
 * @since 2023-04-23
 */
@MappedSuperclass
@Schema(title = "顶级任意抽象类")
@EntityListeners(
  TableRowDeletePersistenceListener::class,
  BizCodeInsertListener::class,
  SnowflakeIdInsertListener::class,
  PreSaveDeleteReferenceListener::class
)
open class AnyEntity : Serializable, Persistable<String> {
  /**
   * id
   */
  @Id
  @Column(name = DataBaseBasicFieldNames.ID)
  @Schema(title = ID, example = "7001234523405")
  private var id: String? = null

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
    val that = other as AnyEntity
    return id != null && "" != id && "null" != id && getId() == that.getId()
  }

  override fun hashCode(): Int {
    return javaClass.hashCode()
  }

  open fun asNew() {
    id = null
  }

  override fun toString(): String {
    return if (id == null) "null" else id!!
  }

  open fun setId(id: String?) {
    this.id = id
  }

  override fun getId(): String? {
    return this.id
  }

  @Transient
  @JsonIgnore
  override fun isNew(): Boolean {
    return id.nonText() || "" == id || "null" == id
  }

  companion object {
    /**
     * 主键
     */
    const val ID = DataBaseBasicFieldNames.ID

    @Serial
    private val serialVersionUID = 1L
  }
}
