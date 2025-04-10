package net.yan100.compose.rds.entities

import jakarta.persistence.Access
import jakarta.persistence.AccessType
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Transient
import java.io.Serializable
import net.yan100.compose.Id
import net.yan100.compose.RefId
import net.yan100.compose.bool
import net.yan100.compose.consts.IDbNames
import net.yan100.compose.domain.ISensitivity
import net.yan100.compose.getDefaultNullableId
import net.yan100.compose.meta.annotations.MetaSkipGeneration
import org.springframework.data.domain.Persistable

/**
 * ## JPA的最基础基类，包括一个 id
 *
 * @author TrueNine
 * @since 2023-04-23
 */
@MappedSuperclass
@Access(AccessType.PROPERTY)
interface IJpaPersistentEntity :
  ISensitivity,
  Persistable<RefId>,
  IExtensionDefineScope,
  IEnhanceEntity,
  Serializable {
  companion object {

    /** 主键 */
    const val ID = IDbNames.ID
  }

  @MetaSkipGeneration
  override val isChangedToSensitiveData: bool
    @Transient get() = super.isChangedToSensitiveData

  /** id */
  @get:Transient @set:Transient var id: Id

  @Suppress("DEPRECATION_ERROR")
  fun toNewEntity() {
    id = getDefaultNullableId()
  }

  override fun recordChangedSensitiveData() {}

  @Suppress("DEPRECATION_ERROR")
  override fun changeWithSensitiveData() {
    super.changeWithSensitiveData()
    require(!isChangedToSensitiveData) { "数据已经脱敏，无需重复执行" }
    this.id = getDefaultNullableId()
    recordChangedSensitiveData()
  }
}
