package net.yan100.compose.core.id

@JvmDefaultWithCompatibility
interface Snowflake {
  fun currentTimeMillis(): Long {
    return System.currentTimeMillis()
  }

  fun nextId(): Long
  fun nextStringId(): String {
    return nextId().toString()
  }
}