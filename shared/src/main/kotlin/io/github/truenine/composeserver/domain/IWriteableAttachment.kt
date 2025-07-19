package io.github.truenine.composeserver.domain

import java.io.OutputStream

interface IWriteableAttachment {
  val name: String
  val outputStream: OutputStream?
  val empty: Boolean
  val size: Long
  val bytes: (() -> ByteArray?)?
    get() = null

  val mediaType: String?
    get() = null

  data class EmptyWriteableAttachment(
    override val name: String,
    override val outputStream: OutputStream? = null,
    override val empty: Boolean = true,
    override val size: Long = 0,
  ) : IWriteableAttachment

  data class DefaultWriteableAttachment(
    override val name: String,
    override val mediaType: String? = null,
    override val outputStream: OutputStream?,
    override val size: Long,
    override val empty: Boolean = size > 0,
  ) : IWriteableAttachment {
    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is DefaultWriteableAttachment) return false
      if (size != other.size) return false
      if (empty != other.empty) return false
      if (name != other.name) return false
      if (mediaType != other.mediaType) return false
      return true
    }

    override fun hashCode(): Int {
      var result = size.hashCode()
      result = 31 * result + empty.hashCode()
      result = 31 * result + name.hashCode()
      result = 31 * result + (mediaType?.hashCode() ?: 0)
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
