package net.yan100.compose.core.lang

import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * # ISO8601 时间戳毫秒标准
 */
val LocalDateTime.iso8601LongUtc: Long get() = this.toInstant(ZoneOffset.UTC).toEpochMilli()

/**
 * # ISO8601 时间戳 秒 标准
 */
val LocalDateTime.iso8601LongUtcSecond: Long
    get() = this.toEpochSecond(ZoneOffset.UTC)
