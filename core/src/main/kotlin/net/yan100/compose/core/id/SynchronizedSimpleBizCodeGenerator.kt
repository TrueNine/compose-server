package net.yan100.compose.core.id

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SynchronizedSimpleBizCodeGenerator(
  private val snowflake: Snowflake
) : BizCodeGenerator {
  override fun nextCodeStr(): String {
    val dt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
    val st = snowflake.nextStringId()
    return "$dt${st.substring(st.length - 4)}"
  }
}
