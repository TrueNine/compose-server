package net.yan100.compose.rds.jimmer.entities

import net.yan100.compose.core.RefId
import net.yan100.compose.rds.core.converters.jimmer.JimmerLongToStringConverter
import net.yan100.compose.rds.jimmer.generators.JimmerSnowflakeLongIdGenerator
import org.babyfish.jimmer.jackson.JsonConverter
import org.babyfish.jimmer.sql.GeneratedValue
import org.babyfish.jimmer.sql.Id
import org.babyfish.jimmer.sql.MappedSuperclass


@MappedSuperclass
interface IPersistentEntity {
  @Id
  @JsonConverter(JimmerLongToStringConverter::class)
  @GeneratedValue(generatorRef = JimmerSnowflakeLongIdGenerator.JIMMER_SNOWFLAKE_LONG_ID_GENERATOR_NAME)
  val id: RefId
}
