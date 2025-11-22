package io.github.truenine.composeserver.depend.jackson.autoconfig

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Jackson configuration properties class
 *
 * Provides centralized Jackson configuration management, supporting core features like timestamp serialization.
 *
 * @author TrueNine
 * @since 2025-01-16
 */
@ConfigurationProperties(prefix = "compose.depend.jackson")
data class JacksonProperties(
  /** Whether to enable timestamp serialization. Defaults to true, serializing all time types as timestamps. */
  var enableTimestampSerialization: Boolean = true,

  /** Timestamp unit configuration. Defaults to using millisecond timestamps. */
  var timestampUnit: TimestampUnit = TimestampUnit.MILLISECONDS,

  /** Serialization inclusion policy. Defaults to not including null values. */
  var serializationInclusion: JsonInclude.Include = JsonInclude.Include.NON_NULL,

  /** Whether to fail on unknown properties. Defaults to false, ignoring unknown properties. */
  var failOnUnknownProperties: Boolean = false,

  /** Whether to write dates as timestamps. Defaults to true, consistent with enableTimestampSerialization. */
  var writeDatesAsTimestamps: Boolean = true,
)

/** Timestamp unit enum */
enum class TimestampUnit {
  /** Millisecond timestamp (default) */
  MILLISECONDS,

  /** Second timestamp */
  SECONDS,
}
