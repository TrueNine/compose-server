package io.github.truenine.composeserver.rds.entities

import io.github.truenine.composeserver.RefId
import io.github.truenine.composeserver.rds.converters.JimmerLongToStringConverter
import io.github.truenine.composeserver.rds.generators.JimmerSnowflakeLongIdGenerator
import org.babyfish.jimmer.jackson.JsonConverter
import org.babyfish.jimmer.sql.*

@MappedSuperclass
interface IPersistentEntity {
  /** Database primary key */
  @Id
  @JsonConverter(JimmerLongToStringConverter::class)
  @GeneratedValue(generatorRef = JimmerSnowflakeLongIdGenerator.Companion.JIMMER_SNOWFLAKE_LONG_ID_GENERATOR_NAME)
  val id: RefId
}
