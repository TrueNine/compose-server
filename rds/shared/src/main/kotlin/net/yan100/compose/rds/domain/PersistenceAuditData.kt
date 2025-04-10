package net.yan100.compose.rds.domain

import java.io.Serializable
import net.yan100.compose.RefId
import net.yan100.compose.bool
import net.yan100.compose.datetime
import net.yan100.compose.i64

open class PersistenceAuditData(
  open val shadowRemoved: bool?,
  open val lockVersion: i64?,
  open val id: RefId?,
  open val createdAt: datetime?,
  open val updatedAt: datetime?,
) : Serializable {
  @Deprecated("不建议直接调用", level = DeprecationLevel.WARNING)
  constructor() : this(null, null, null, null, null)

  open operator fun component1(): RefId? = id

  open operator fun component2(): datetime? = createdAt

  open operator fun component3(): datetime? = updatedAt

  open operator fun component4(): bool? = shadowRemoved

  open operator fun component5(): i64? = lockVersion

  override fun toString(): String {
    return "id=$id, createdAt=$createdAt, updatedAt=$updatedAt, shadowRemoved=$shadowRemoved, lockVersion=$lockVersion"
  }
}
