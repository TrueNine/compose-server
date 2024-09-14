package net.yan100.compose.security.crypto

import java.nio.charset.Charset
import java.util.*

/**
 * base64 工具类
 *
 * @author TrueNine
 * @since 2023-02-20
 */
interface IBase64 {
  companion object {
    private val encoder = Base64.getEncoder()
    private val decoder = Base64.getDecoder()
    private val defaultCharset = Charsets.UTF_8

    /**
     * 编码
     *
     * @param content 内容
     * @return [String]
     */
    @JvmStatic
    fun encode(content: ByteArray, charset: Charset = defaultCharset): String {
      return String(encoder.encode(content))
    }

    /**
     * 以字节编码
     *
     * @param content 内容
     * @return [byte[]]
     */
    @JvmStatic
    fun encodeToByte(content: ByteArray): ByteArray {
      return encoder.encode(content)
    }

    /**
     * 解码字节
     *
     * @param base64 base64
     * @return [byte[]]
     */
    @JvmStatic
    fun decodeToByte(base64: String): ByteArray {
      return decoder.decode(base64)
    }

    /**
     * 解码字节
     *
     * @param base64 base64
     * @return [byte[]]
     */
    @JvmStatic
    fun decode(base64: String, charset: Charset = defaultCharset): String {
      return String(decoder.decode(base64), charset)
    }

    @JvmStatic
    fun decode(base64: ByteArray, charset: Charset = defaultCharset): String {
      return decoder.decode(base64).toString(charset)
    }
  }
}
