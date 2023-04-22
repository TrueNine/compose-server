package com.truenine.component.core.id

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SynchronizedSimpleBizCode(
  private val snowflake: Snowflake
) : BizCode {

  override fun nextCode(): Long = nextCodeStr().toLong()

  override fun nextCodeStr(): String {
    val dt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
    val st = snowflake.nextStr()
    return "$dt${st.substring(st.length - 4)}"
  }
}
