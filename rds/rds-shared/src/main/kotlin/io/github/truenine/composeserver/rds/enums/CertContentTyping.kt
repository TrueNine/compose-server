package io.github.truenine.composeserver.rds.enums

import io.github.truenine.composeserver.IIntEnum
import org.babyfish.jimmer.sql.EnumItem
import org.babyfish.jimmer.sql.EnumType

/** Certificate content type */
@EnumType(EnumType.Strategy.ORDINAL)
enum class CertContentTyping(private val v: Int) : IIntEnum {
  /** No requirement */
  @EnumItem(ordinal = 0) NONE(0),

  /** Image */
  @EnumItem(ordinal = 1) IMAGE(1),

  /** Scanned image */
  @EnumItem(ordinal = 2) SCANNED_IMAGE(2),

  /** Screenshot */
  @EnumItem(ordinal = 3) SCREEN_SHOT(3),

  /** Video */
  @EnumItem(ordinal = 4) VIDEO(4),

  /** Audio recording */
  @EnumItem(ordinal = 5) RECORDING(5),

  /** Photocopy image */
  @EnumItem(ordinal = 6) COPYFILE_IMAGE(6),

  /** Re-shot image */
  @EnumItem(ordinal = 7) REMAKE_IMAGE(7),

  /** Processed scanned image */
  @EnumItem(ordinal = 8) PROCESSED_SCANNED_IMAGE(8),

  /** Processed image */
  @EnumItem(ordinal = 9) PROCESSED_IMAGE(9),

  /** Processed video */
  @EnumItem(ordinal = 10) PROCESSED_VIDEO(10),

  /** Processed audio */
  @EnumItem(ordinal = 11) PROCESSED_AUDIO(11);

  override val value: Int = v

  companion object {
    @JvmStatic fun findVal(v: Int?) = CertContentTyping.entries.find { it.value == v }

    @JvmStatic operator fun get(v: Int?) = findVal(v)
  }
}
