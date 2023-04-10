package com.truenine.component.core.id

@JvmDefaultWithCompatibility
interface Snowflake {
  fun currentTimeMillis(): Long {
    return System.currentTimeMillis()
  }

  fun nextId(): Long
  fun nextStr(): String {
    return nextId().toString()
  }
}
