package io.github.truenine.composeserver.rds.generators

import io.github.truenine.composeserver.generator.ISnowflakeGenerator
import org.babyfish.jimmer.sql.meta.UserIdGenerator

class JimmerSnowflakeStringIdGenerator(private var snowflake: ISnowflakeGenerator) : UserIdGenerator<String> {
  companion object {
    const val JIMMER_SNOWFLAKE_STRING_ID_GENERATOR_NAME = "jimmerSnowflakeStringIdGenerator"
  }

  override fun generate(entityType: Class<*>?): String {
    return snowflake.nextString()
  }
}
