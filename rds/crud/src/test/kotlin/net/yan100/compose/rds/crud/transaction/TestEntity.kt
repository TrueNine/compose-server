package net.yan100.compose.rds.crud.transaction

import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.GeneratedValue
import org.babyfish.jimmer.sql.GenerationType
import org.babyfish.jimmer.sql.Id

@Entity
interface TestEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long
  val name: String?
  val value: Int?
}
