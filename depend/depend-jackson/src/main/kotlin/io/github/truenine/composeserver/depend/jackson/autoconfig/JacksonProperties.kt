package io.github.truenine.composeserver.depend.jackson.autoconfig

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Jackson配置属性类
 *
 * 提供统一的Jackson配置管理，支持时间戳序列化等核心功能的配置
 *
 * @author TrueNine
 * @since 2025-01-16
 */
@ConfigurationProperties(prefix = "compose.depend.jackson")
data class JacksonProperties(
  /** 是否启用时间戳序列化 默认为true，将所有时间类型序列化为时间戳 */
  var enableTimestampSerialization: Boolean = true,

  /** 时间戳单位配置 默认使用毫秒时间戳 */
  var timestampUnit: TimestampUnit = TimestampUnit.MILLISECONDS,

  /** 序列化包含策略 默认不包含null值 */
  var serializationInclusion: JsonInclude.Include = JsonInclude.Include.NON_NULL,

  /** 遇到未知属性时是否失败 默认为false，忽略未知属性 */
  var failOnUnknownProperties: Boolean = false,

  /** 是否将日期写为时间戳 默认为true，与enableTimestampSerialization保持一致 */
  var writeDatesAsTimestamps: Boolean = true,
)

/** 时间戳单位枚举 */
enum class TimestampUnit {
  /** 毫秒时间戳（默认） */
  MILLISECONDS,

  /** 秒时间戳 */
  SECONDS,
}
