package net.yan100.compose.core.domain

import java.io.OutputStream
import net.yan100.compose.core.bool
import net.yan100.compose.core.i64

interface IWriteableAttachment {
  val name: String
  val outputStream: OutputStream?
  val empty: bool
  val size: i64
  val bytes: (() -> ByteArray?)?
    get() = null

  val mimeType: String?
    get() = null

  data class EmptyWriteableAttachment(
    override val name: String,
    override val outputStream: OutputStream? = null,
    override val empty: bool = true,
    override val size: i64 = 0,
  ) : IWriteableAttachment

  data class DefaultWriteableAttachment(
    override val name: String,
    override val mimeType: String? = null,
    override val outputStream: OutputStream?,
    override val size: i64,
    override val empty: bool = size > 0,
  ) : IWriteableAttachment {
    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is DefaultWriteableAttachment) return false
      if (size != other.size) return false
      if (empty != other.empty) return false
      if (name != other.name) return false
      if (mimeType != other.mimeType) return false
      return true
    }

    override fun hashCode(): Int {
      var result = size.hashCode()
      result = 31 * result + empty.hashCode()
      result = 31 * result + name.hashCode()
      result = 31 * result + (mimeType?.hashCode() ?: 0)
      return result
    }
  }

  companion object {
    @JvmStatic
    fun empty(name: String = ""): IWriteableAttachment {
      return EmptyWriteableAttachment(name)
    }
  }
}
