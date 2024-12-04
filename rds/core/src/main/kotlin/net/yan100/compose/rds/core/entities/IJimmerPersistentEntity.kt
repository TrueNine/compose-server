package net.yan100.compose.rds.core.entities

import net.yan100.compose.core.RefId
import net.yan100.compose.rds.jimmer.generators.JimmerSnowflakeStringIdGenerator
import org.babyfish.jimmer.sql.GeneratedValue
import org.babyfish.jimmer.sql.Id
import org.babyfish.jimmer.sql.MappedSuperclass


@MappedSuperclass
interface IJimmerPersistentEntity {
  @Id
  @GeneratedValue(generatorRef = JimmerSnowflakeStringIdGenerator.JIMMER_SNOWFLAKE_STRING_ID_GENERATOR_NAME)
  val id: RefId
}
