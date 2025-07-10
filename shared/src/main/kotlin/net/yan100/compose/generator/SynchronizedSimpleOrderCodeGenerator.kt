package net.yan100.compose.generator

import java.time.format.DateTimeFormatter
import net.yan100.compose.datetime

class SynchronizedSimpleOrderCodeGenerator(private val snowflake: ISnowflakeGenerator) : IOrderCodeGenerator {
  private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")

  override fun nextString(): String {
    val dt = datetime.now().format(dateTimeFormatter)
    val st = snowflake.nextString()
    return "$dt${st}"
  }
}
