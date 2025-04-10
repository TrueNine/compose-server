package net.yan100.compose.rds.generators

import net.yan100.compose.RefId
import net.yan100.compose.generator.ISnowflakeGenerator
import org.babyfish.jimmer.sql.meta.UserIdGenerator

class JimmerSnowflakeLongIdGenerator(
  private var snowflake: ISnowflakeGenerator
) : UserIdGenerator<RefId> {
  companion object {
    const val JIMMER_SNOWFLAKE_LONG_ID_GENERATOR_NAME =
      "jimmerSnowflakeLongIdGenerator"
  }

  override fun generate(entityType: Class<*>): Long {
    return snowflake.next()
  }
}
