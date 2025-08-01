package io.github.truenine.composeserver.testtoolkit.properties

import org.springframework.boot.context.properties.ConfigurationProperties

/** # 测试工具包配置属性 */
@ConfigurationProperties(prefix = "compose.testtoolkit")
data class TestConfigurationProperties(
  /** 是否启用测试配置 */
  val enabled: Boolean = true,

  /** 是否关闭条件评估报告 */
  val disableConditionEvaluationReport: Boolean = true,

  /** 是否启用虚拟线程 */
  val enableVirtualThreads: Boolean = true,

  /** ANSI 颜色输出模式 */
  val ansiOutputMode: AnsiOutputMode = AnsiOutputMode.ALWAYS,

  /** 额外的测试属性 */
  val additionalProperties: Map<String, String> = emptyMap(),
)

/**
 * # ANSI 颜色输出模式
 *
 * 定义控制台颜色输出的不同模式
 */
enum class AnsiOutputMode(val value: String) {
  /** 从不使用颜色输出 */
  NEVER("never"),

  /** 自动检测是否支持颜色输出 */
  DETECT("detect"),

  /** 总是使用颜色输出 */
  ALWAYS("always");

  override fun toString(): String = value
}
