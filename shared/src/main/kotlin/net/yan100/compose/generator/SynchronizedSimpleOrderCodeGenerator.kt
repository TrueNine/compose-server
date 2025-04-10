package net.yan100.compose.generator

import net.yan100.compose.datetime
import java.time.format.DateTimeFormatter

class SynchronizedSimpleOrderCodeGenerator(
  private val snowflake: ISnowflakeGenerator,
) : IOrderCodeGenerator {
  private val dateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")

  override fun nextString(): String {
    val dt = datetime.now().format(dateTimeFormatter)
    val st = snowflake.nextString()
    return "$dt${st}"
  }
}
