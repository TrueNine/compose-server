package net.yan100.compose.rds.core.generators

import net.yan100.compose.core.generator.ISnowflakeGenerator
import org.babyfish.jimmer.sql.meta.UserIdGenerator

class JimmerSnowflakeStringIdGenerator(
  private var snowflake: ISnowflakeGenerator
) : UserIdGenerator<String> {
  companion object {
    const val JIMMER_SNOWFLAKE_STRING_ID_GENERATOR_NAME =
      "jimmerSnowflakeStringIdGenerator"
  }

  override fun generate(entityType: Class<*>?): String {
    return snowflake.nextString()
  }
}
