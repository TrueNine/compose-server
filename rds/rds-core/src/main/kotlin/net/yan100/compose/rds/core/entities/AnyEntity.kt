package net.yan100.compose.rds.core.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Transient
import net.yan100.compose.core.alias.Id
import net.yan100.compose.core.consts.DataBaseBasicFieldNames
import net.yan100.compose.core.lang.nonText
import net.yan100.compose.rds.core.listener.BizCodeInsertListener
import net.yan100.compose.rds.core.listener.PreSaveDeleteReferenceListener
import net.yan100.compose.rds.core.listener.SnowflakeIdInsertListener
import net.yan100.compose.rds.core.listener.TableRowDeletePersistenceListener
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
open class AnyEntity : Serializable, Persistable<Id> {
  /**
   * id
   */
  @jakarta.persistence.Id
  @Column(name = DataBaseBasicFieldNames.ID)
  @Schema(title = ID, example = "7001234523405")
  private var id: Id? = null

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
    return id != null && "" != id && "null" != id && id == (other as AnyEntity).id
  }

  override fun hashCode(): Int {
    return javaClass.hashCode()
  }

  open fun asNew() {
    id = null
  }

  protected fun withToString(superString: String, vararg properties: Pair<String, Any?>): String {
    return superString + "[" + properties.joinToString(",") { "${it.first}=" + (it.second?.toString() ?: "null") } + "]"
  }

  override fun toString(): String {
    return withToString("", "id" to id)
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

/**
 * 将自身置空为新的 Entity 对象
 */
fun <T : AnyEntity> T.withNew(): T {
  this.asNew()
  return this
}