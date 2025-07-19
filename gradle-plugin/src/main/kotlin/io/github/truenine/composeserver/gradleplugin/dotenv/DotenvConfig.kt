package io.github.truenine.composeserver.gradleplugin.dotenv

/**
 * # Dotenv 环境变量配置
 *
 * 用于配置从 .env 文件加载环境变量的相关选项
 *
 * @author TrueNine
 * @since 2024-12-19
 */
open class DotenvConfig {

  /** 是否启用 dotenv 环境变量加载功能 */
  var enabled: Boolean = false

  /** dotenv 文件路径，支持绝对路径和相对路径（相对于项目根目录） */
  var filePath: String = ""

  /** 是否在文件不存在时显示警告日志 */
  var warnOnMissingFile: Boolean = true

  /** 是否在解析错误时显示详细错误信息 */
  var verboseErrors: Boolean = true

  /** 是否覆盖已存在的环境变量 */
  var overrideExisting: Boolean = false

  /** 是否忽略空值 */
  var ignoreEmptyValues: Boolean = false

  /** 环境变量名前缀过滤器，只加载指定前缀的变量 */
  var prefixFilter: String? = null

  /** 排除的环境变量名列表 */
  var excludeKeys: MutableSet<String> = mutableSetOf()

  /** 只包含的环境变量名列表，如果设置则只加载这些变量 */
  var includeKeys: MutableSet<String> = mutableSetOf()

  /**
   * 设置 dotenv 文件路径
   *
   * @param path 文件路径，支持绝对路径和相对路径
   */
  fun filePath(path: String) {
    filePath = path
  }

  /**
   * 设置是否在文件不存在时显示警告
   *
   * @param warn 是否显示警告
   */
  fun warnOnMissingFile(warn: Boolean) {
    warnOnMissingFile = warn
  }

  /**
   * 设置是否显示详细错误信息
   *
   * @param verbose 是否显示详细信息
   */
  fun verboseErrors(verbose: Boolean) {
    verboseErrors = verbose
  }

  /**
   * 设置是否覆盖已存在的环境变量
   *
   * @param override 是否覆盖
   */
  fun overrideExisting(override: Boolean) {
    overrideExisting = override
  }

  /**
   * 设置是否忽略空值
   *
   * @param ignore 是否忽略
   */
  fun ignoreEmptyValues(ignore: Boolean) {
    ignoreEmptyValues = ignore
  }

  /**
   * 设置环境变量名前缀过滤器
   *
   * @param prefix 前缀字符串
   */
  fun prefixFilter(prefix: String?) {
    prefixFilter = prefix
  }

  /**
   * 添加要排除的环境变量名
   *
   * @param keys 要排除的变量名
   */
  fun excludeKeys(vararg keys: String) {
    excludeKeys.addAll(keys)
  }

  /**
   * 设置只包含的环境变量名列表
   *
   * @param keys 只包含的变量名
   */
  fun includeKeys(vararg keys: String) {
    includeKeys.addAll(keys)
  }

  /** 清空排除列表 */
  fun clearExcludeKeys() {
    excludeKeys.clear()
  }

  /** 清空包含列表 */
  fun clearIncludeKeys() {
    includeKeys.clear()
  }

  /**
   * 检查配置是否有效
   *
   * @return 配置是否有效
   */
  fun isValid(): Boolean {
    return enabled && filePath.isNotBlank()
  }

  /**
   * 获取配置摘要信息
   *
   * @return 配置摘要字符串
   */
  fun getSummary(): String {
    return buildString {
      append("DotenvConfig(")
      append("enabled=$enabled, ")
      append("filePath='$filePath', ")
      append("warnOnMissingFile=$warnOnMissingFile, ")
      append("verboseErrors=$verboseErrors, ")
      append("overrideExisting=$overrideExisting, ")
      append("ignoreEmptyValues=$ignoreEmptyValues")
      prefixFilter?.let { append(", prefixFilter='$it'") }
      if (excludeKeys.isNotEmpty()) append(", excludeKeys=$excludeKeys")
      if (includeKeys.isNotEmpty()) append(", includeKeys=$includeKeys")
      append(")")
    }
  }
}
