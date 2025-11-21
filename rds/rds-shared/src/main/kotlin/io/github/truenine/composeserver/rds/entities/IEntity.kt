package io.github.truenine.composeserver.rds.entities

import io.github.truenine.composeserver.instant
import org.babyfish.jimmer.sql.Column
import org.babyfish.jimmer.sql.Default
import org.babyfish.jimmer.sql.LogicalDeleted
import org.babyfish.jimmer.sql.MappedSuperclass
import org.babyfish.jimmer.sql.Version

@MappedSuperclass
interface IEntity : IPersistentEntity {
  /** Field creation time */
  @Column(name = "crd") val crd: instant?

  /** Field modification time */
  @Column(name = "mrd") val mrd: instant?

  /** Optimistic lock version */
  @Version @Default("0") @Column(name = "rlv") val rlv: Int

  /** Logical delete flag */
  @LogicalDeleted("now") @Column(name = "ldf") val ldf: instant?
}
