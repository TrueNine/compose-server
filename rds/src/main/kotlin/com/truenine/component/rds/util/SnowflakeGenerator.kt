package com.truenine.component.rds.util

import cn.hutool.core.lang.Snowflake

object SnowflakeGenerator {
  // TODO 有待考证
  private val snowflake = Snowflake(
    1,
    1
  )

  fun nextStr(): String {
    return snowflake.nextIdStr()
  }
}
