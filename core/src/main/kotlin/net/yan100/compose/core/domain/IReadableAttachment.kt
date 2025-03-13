package net.yan100.compose.core.domain

import java.io.InputStream
import java.io.Serializable
import net.yan100.compose.core.bool
import net.yan100.compose.core.i64
import net.yan100.compose.core.string

/**
 * # 附件表示形式
 * 遵循 spring boot 的 multipartFile
 *
 * @see [org.springframework.web.multipart.MultipartFile]
 */
interface IReadableAttachment : Serializable {
  val name: string
  val mimeType: string?
    get() = null

  val empty: bool
  val size: i64
  val bytes: (() -> ByteArray?)?
    get() = null

  val inputStream: InputStream?
    get() = null

  data class EmptyReadableAttachment(
    override val name: string,
    override val empty: bool = true,
    override val size: i64 = 0L,
    override val inputStream: InputStream? = null,
  ) : IReadableAttachment

  data class DefaultReadableAttachment(
    override val name: string,
    override val mimeType: string? = null,
    override val empty: bool = false,
    override val size: i64 = 0L,
    override val bytes: (() -> ByteArray?)? = null,
    override val inputStream: InputStream? = null,
  ) : IReadableAttachment {
    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is DefaultReadableAttachment) return false
      if (empty != other.empty) return false
      if (size != other.size) return false
      if (name != other.name) return false
      if (mimeType != other.mimeType) return false
      return true
    }

    override fun hashCode(): Int {
      var result = empty.hashCode()
      result = 31 * result + size.hashCode()
      result = 31 * result + name.hashCode()
      result = 31 * result + (mimeType?.hashCode() ?: 0)
      return result
    }
  }

  companion object {
    fun empty(name: string = ""): IReadableAttachment {
      return EmptyReadableAttachment(name = name)
    }
  }
}
