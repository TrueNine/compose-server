package io.github.truenine.composeserver.rds.entities

import io.github.truenine.composeserver.RefId
import io.github.truenine.composeserver.rds.converters.JimmerLongToStringConverter
import io.github.truenine.composeserver.rds.generators.JimmerSnowflakeLongIdGenerator
import org.babyfish.jimmer.jackson.JsonConverter
import org.babyfish.jimmer.sql.GeneratedValue
import org.babyfish.jimmer.sql.Id
import org.babyfish.jimmer.sql.MappedSuperclass

@MappedSuperclass
interface IPersistentEntity {
  /** 数据库主键 */
  @Id
  @JsonConverter(JimmerLongToStringConverter::class)
  @GeneratedValue(generatorRef = JimmerSnowflakeLongIdGenerator.Companion.JIMMER_SNOWFLAKE_LONG_ID_GENERATOR_NAME)
  val id: RefId
}
