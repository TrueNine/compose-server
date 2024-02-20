package net.yan100.compose.rds.core.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Transient
import net.yan100.compose.core.alias.Id
import net.yan100.compose.core.consts.DataBaseBasicFieldNames
import net.yan100.compose.rds.core.listener.BizCodeInsertListener
import net.yan100.compose.rds.core.listener.PreSaveDeleteReferenceListener
import net.yan100.compose.rds.core.listener.SnowflakeIdInsertListener
import net.yan100.compose.rds.core.listener.TableRowDeletePersistenceListener
import net.yan100.compose.rds.core.models.PagedRequestParam
import org.hibernate.Hibernate
import org.springframework.data.domain.Persistable
import java.io.Serial

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
abstract class AnyEntity : Persistable<Id>, PageableEntity, PagedRequestParam() {
    companion object {
        /**
         * 主键
         */
        const val ID = DataBaseBasicFieldNames.ID

        @Serial
        private val serialVersionUID = 1L
    }

    /**
     * id
     */
    @jakarta.persistence.Id
    @Column(name = DataBaseBasicFieldNames.ID)
    @Schema(title = ID, examples = ["7001234523405", "7001234523441"])
    private var id: Id? = null
        @Transient
        @JsonIgnore
        @JvmName("setKotlinInternalId")
        set
        @Transient
        @JsonIgnore
        @JvmName("getKotlinInternalId")
        get() = field ?: ""

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        return "" != id && id != null && "null" != id && id == (other as AnyEntity).id
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    @Transient
    @JsonIgnore
    fun asNew() {
        this.id = null
    }

    @Transient
    @JsonIgnore
    fun withToString(superString: String, vararg properties: Pair<String, Any?>): String {
        return superString + "[" + properties.joinToString(",") { "${it.first}=" + (it.second?.toString() ?: "null") } + "]"
    }

    fun setId(id: String) {
        this.id = id
    }

    override fun getId(): String {
        return this.id ?: ""
    }

    @Transient
    @JsonIgnore
    override fun isNew(): Boolean {
        return "" == id || null == id
    }
}

/**
 * 将自身置空为新的 Entity 对象
 */
fun <T : AnyEntity> T.withNew(): T {
    asNew()
    return this
}

inline fun <T : AnyEntity> T.withNew(crossinline after: (T) -> T): T = after(withNew())


/**
 * 将集合内的所有元素置空为新的 Entity 对象
 */
fun <T : AnyEntity> List<T>.withNew(): List<T> = map { it.withNew() }
inline fun <T : AnyEntity> List<T>.withNew(crossinline after: (List<T>) -> List<T>): List<T> = after(this.map { it.withNew() })
inline fun <T : AnyEntity, R : Any> List<T>.withNewMap(crossinline after: (List<T>) -> List<R>): List<R> = after(this.withNew())
