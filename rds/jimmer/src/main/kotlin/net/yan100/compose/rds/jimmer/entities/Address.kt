package net.yan100.compose.rds.jimmer.entities

import net.yan100.compose.core.*
import net.yan100.compose.core.consts.IDbNames
import org.babyfish.jimmer.Formula
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.IdView
import org.babyfish.jimmer.sql.JoinColumn
import org.babyfish.jimmer.sql.OneToOne


@Entity
interface Address : ITreeEntity {
  @Formula(dependencies = ["rln", "rrn"])
  val parentLevel: i32 get() = (rrn - rln - 1).toInt()

  @OneToOne
  @JoinColumn(name = IDbNames.ROW_PARENT_ID)
  val parentAddr: Address?

  /**
   * 父 id
   */
  @IdView("parentAddr")
  val rpi: RefId?

  val name: string
  val code: string
  val center: string?
  val yearVersion: string?
  val level: i64
  val leaf: bool?
}
