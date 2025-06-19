package net.yan100.compose.testtoolkit.autoconfig

/**
 * # ANSI 颜色输出模式
 *
 * 定义控制台颜色输出的不同模式
 */
enum class AnsiOutputMode(val value: String) {
  /**
   * 从不使用颜色输出
   */
  NEVER("never"),

  /**
   * 自动检测是否支持颜色输出
   */
  DETECT("detect"),

  /**
   * 总是使用颜色输出
   */
  ALWAYS("always");

  override fun toString(): String = value
} 