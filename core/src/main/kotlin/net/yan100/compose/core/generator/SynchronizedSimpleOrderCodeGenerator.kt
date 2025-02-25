package net.yan100.compose.core.generator

import java.time.format.DateTimeFormatter
import net.yan100.compose.core.datetime

class SynchronizedSimpleOrderCodeGenerator(
  private val snowflake: ISnowflakeGenerator
) : IOrderCodeGenerator {

  override fun nextString(): String {
    val dt =
      datetime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
    val st = snowflake.nextString()
    return "$dt${st.substring(st.length - 4)}"
  }
}
