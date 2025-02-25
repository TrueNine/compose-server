package net.yan100.compose.security.crypto

import java.security.Key

/**
 * # pem 格式 的字符串解析格式
 * 以 -----BEGIN keyType----- 开头 内容以每行 64 个字符填充 以 -----END keyType----- 结尾
 */
class PemFormat(private val pem: String) {
  companion object {
    const val SEPARATOR = "-----"
    const val LINE_LENGTH = 64
    const val BEGIN_START = "${SEPARATOR}BEGIN "
    const val END_START = "${SEPARATOR}END "

    @JvmStatic
    operator fun get(key: Key, keyType: String? = null): String =
      get(
        key.encoded.encodeBase64String,
        keyType ?: "${key.algorithm} ${key.format ?: ""}",
      )

    @JvmStatic
    operator fun get(base64: String, keyType: String? = null): String {
      val trim = base64.trim()
      val a =
        trim
          .replace("\r", "\n")
          .windowed(LINE_LENGTH, LINE_LENGTH, true)
          .joinToString(System.lineSeparator())
      return "$BEGIN_START${keyType?.uppercase()?.trim() ?: ""}$SEPARATOR" +
        System.lineSeparator() +
        a +
        System.lineSeparator() +
        "$END_START${keyType?.uppercase()?.trim() ?: ""}$SEPARATOR"
    }
  }

  private val triedKey: String =
    pem.trim().replace("\r", "\n").replace("\n\n", "\n")
  var schema: String
  private var endSchema: String
  var content: String

  init {
    check(triedKey.startsWith(BEGIN_START)) { "该key 非key 文件" }
    check(triedKey.endsWith(SEPARATOR)) { "该key 非key 文件" }
    val beginAndEnd =
      triedKey.split("\n").let {
        content = it.drop(1).dropLast(1).joinToString("")
        it.first() to it.last()
      }
    schema =
      beginAndEnd.first
        .substring(BEGIN_START.length)
        .replace(SEPARATOR, "")
        .trim()
    endSchema =
      beginAndEnd.second
        .substring(END_START.length)
        .replace(SEPARATOR, "")
        .trim()
    check(schema == endSchema) { "传入的pem 格式不正确，开头与结尾类型不一致" }
  }
}
