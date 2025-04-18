package net.yan100.compose.security.crypto

import java.security.Key
import java.security.PrivateKey
import java.security.PublicKey

/**
 * PEM 格式字符串解析器
 *
 * 格式规范:
 * - 以 -----BEGIN keyType----- 开头
 * - 内容以每行 64 个字符填充
 * - 以 -----END keyType----- 结尾
 */
class PemFormat private constructor(
  private val rawPem: String,
) {

  companion object {
    const val SEPARATOR = "-----"
    const val LINE_LENGTH = 64
    const val BEGIN_PREFIX = "${SEPARATOR}BEGIN "
    const val END_PREFIX = "${SEPARATOR}END "

    @JvmStatic
    val LINE_SEPARATOR: String = System.lineSeparator()

    const val DEFAULT_STRING_BUILDER_CAPACITY = 1024

    /**
     * 从密钥对象创建 PEM 格式字符串
     *
     * @param key 密钥对象
     * @param keyType 可选的密钥类型，如果为空则使用密钥算法和格式
     * @return PEM 格式字符串
     * @throws IllegalArgumentException 如果密钥编码失败
     */
    @JvmStatic
    @Throws(IllegalArgumentException::class)
    operator fun get(key: Key, keyType: String? = null): String {
      val encoded = requireNotNull(key.encoded) { "密钥编码失败" }
      val algorithm = requireNotNull(key.algorithm) { "密钥算法不能为空" }
      
      val defaultType = buildString {
        append(algorithm)
        if (key is PrivateKey) {
          append(" PRIVATE")
        } else if (key is PublicKey) {
          append(" PUBLIC")
        }
        append(" KEY")
      }
      
      return get(encoded.encodeBase64String, keyType ?: defaultType)
    }

    /**
     * 从 Base64 字符串创建 PEM 格式字符串
     *
     * @param base64 Base64 编码的内容
     * @param keyType 可选的密钥类型
     * @return PEM 格式字符串
     */
    @JvmStatic
    operator fun get(base64: String, keyType: String? = null): String {
      require(base64.isNotBlank()) { "Base64 内容不能为空" }

      val normalizedType = keyType?.uppercase()?.trim().orEmpty()
      val formattedContent = base64.trim().replace("\r", "\n").chunked(LINE_LENGTH).joinToString(LINE_SEPARATOR)

      return StringBuilder(DEFAULT_STRING_BUILDER_CAPACITY).apply {
        append(BEGIN_PREFIX)
        append(normalizedType)
        append(SEPARATOR)
        append(LINE_SEPARATOR)
        append(formattedContent)
        append(LINE_SEPARATOR)
        append(END_PREFIX)
        append(normalizedType)
        append(SEPARATOR)
      }.toString()
    }

    /**
     * 创建 PEM 格式解析器实例
     *
     * @param pem PEM 格式的字符串
     * @return PEM 格式解析器实例
     * @throws IllegalArgumentException 如果输入的 PEM 格式不正确
     */
    @Throws(IllegalArgumentException::class)
    fun parse(pem: String): PemFormat {
      require(pem.isNotBlank()) { "PEM 内容不能为空" }
      require(pem.contains(BEGIN_PREFIX)) { "无效的 PEM 格式：缺少 BEGIN 标记" }
      require(pem.contains(END_PREFIX)) { "无效的 PEM 格式：缺少 END 标记" }
      
      val lines = pem.trim().lines()
      require(lines.size >= 3) { "无效的 PEM 格式：内容不完整" }
      
      val beginLine = lines.first()
      val endLine = lines.last()
      
      require(beginLine.startsWith(BEGIN_PREFIX)) { "无效的 PEM 格式：BEGIN 标记格式错误" }
      require(endLine.startsWith(END_PREFIX)) { "无效的 PEM 格式：END 标记格式错误" }
      
      val beginSchema = beginLine.substring(BEGIN_PREFIX.length).removeSuffix(SEPARATOR).trim()
      val endSchema = endLine.substring(END_PREFIX.length).removeSuffix(SEPARATOR).trim()
      
      require(beginSchema.isNotBlank()) { "PEM 类型标识不能为空" }
      require(endSchema.isNotBlank()) { "PEM 结束类型标识不能为空" }
      require(beginSchema == endSchema) { "PEM 格式错误：BEGIN 类型「$beginSchema」与 END 类型「$endSchema」不匹配" }
      
      return PemFormat(pem)
    }
  }

  // 延迟初始化属性，避免在构造函数中进行复杂计算
  private val normalizedPem: String by lazy {
    rawPem.trim().replace("\r", "\n").replace("\n\n", "\n")
  }

  /**
   * PEM 格式的类型标识
   */
  val schema: String by lazy {
    extractSchema()
  }

  /**
   * PEM 格式的内容部分（不含头尾标记）
   */
  val content: String by lazy {
    extractContent()
  }

  private fun extractSchema(): String {
    require(normalizedPem.isNotBlank()) { "PEM 内容不能为空" }
    require(normalizedPem.startsWith(BEGIN_PREFIX)) { "无效的 PEM 格式：缺少 BEGIN 标记" }
    require(normalizedPem.endsWith(SEPARATOR)) { "无效的 PEM 格式：缺少结束标记" }

    val lines = normalizedPem.lines()
    require(lines.size >= 3) { "无效的 PEM 格式：内容不完整" }

    val beginLine = lines.first()
    val endLine = lines.last()

    val beginSchema = beginLine.substring(BEGIN_PREFIX.length).removeSuffix(SEPARATOR).trim()
    require(beginSchema.isNotBlank()) { "PEM 类型标识不能为空" }

    val endSchema = endLine.substring(END_PREFIX.length).removeSuffix(SEPARATOR).trim()
    require(endSchema.isNotBlank()) { "PEM 结束类型标识不能为空" }

    require(beginSchema == endSchema) {
      "PEM 格式错误：BEGIN 类型「$beginSchema」与 END 类型「$endSchema」不匹配"
    }
    return beginSchema
  }

  private fun extractContent(): String {
    val lines = normalizedPem.lines()
    val content = lines.drop(1).dropLast(1).joinToString("")
    require(content.isNotBlank()) { "PEM 内容部分不能为空" }
    return content
  }
}
