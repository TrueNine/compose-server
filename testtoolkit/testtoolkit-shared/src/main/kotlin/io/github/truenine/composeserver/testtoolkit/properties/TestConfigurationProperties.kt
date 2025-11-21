package io.github.truenine.composeserver.testtoolkit.properties

import io.github.truenine.composeserver.testtoolkit.SpringBootConfigurationPropertiesPrefixes
import org.springframework.boot.context.properties.ConfigurationProperties

/** Test toolkit configuration properties. */
@ConfigurationProperties(prefix = SpringBootConfigurationPropertiesPrefixes.TESTTOOLKIT)
data class TestConfigurationProperties(
  /** Whether test configuration is enabled. */
  var enabled: Boolean = true,

  /** Whether to disable the condition evaluation report. */
  var disableConditionEvaluationReport: Boolean = true,

  /** Whether to enable virtual threads. */
  var enableVirtualThreads: Boolean = true,

  /** ANSI color output mode. */
  var ansiOutputMode: AnsiOutputMode = AnsiOutputMode.ALWAYS,

  /** Additional test properties. */
  var additionalProperties: Map<String, String> = emptyMap(),
)

/**
 * ANSI color output mode.
 *
 * Defines different modes for console color output.
 */
enum class AnsiOutputMode(val value: String) {
  /** Never use colored output. */
  NEVER("never"),

  /** Automatically detect whether color output is supported. */
  DETECT("detect"),

  /** Always use colored output. */
  ALWAYS("always");

  override fun toString(): String = value
}
