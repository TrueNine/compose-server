package io.github.truenine.composeserver.rds.crud.transaction

import org.babyfish.jimmer.sql.*

@Entity
interface TestEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long
  val name: String?
  val value: Int?
}
