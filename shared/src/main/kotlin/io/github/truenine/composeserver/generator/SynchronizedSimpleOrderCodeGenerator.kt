package io.github.truenine.composeserver.generator

import io.github.truenine.composeserver.datetime
import java.time.format.DateTimeFormatter

class SynchronizedSimpleOrderCodeGenerator(private val snowflake: ISnowflakeGenerator) : IOrderCodeGenerator {
  private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")

  override fun nextString(): String {
    val dt = datetime.now().format(dateTimeFormatter)
    val st = snowflake.nextString()
    return "$dt${st}"
  }
}
