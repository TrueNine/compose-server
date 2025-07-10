package net.yan100.compose.rds.typing

import net.yan100.compose.typing.IntTyping
import org.babyfish.jimmer.sql.EnumItem
import org.babyfish.jimmer.sql.EnumType

/** 证件内容类型 */
@EnumType(EnumType.Strategy.ORDINAL)
enum class CertContentTyping(private val v: Int) : IntTyping {
  /** 无要求 */
  @EnumItem(ordinal = 0) NONE(0),

  /** 图片 */
  @EnumItem(ordinal = 1) IMAGE(1),

  /** 扫描件图片 */
  @EnumItem(ordinal = 2) SCANNED_IMAGE(2),

  /** 截图 */
  @EnumItem(ordinal = 3) SCREEN_SHOT(3),

  /** 视频 */
  @EnumItem(ordinal = 4) VIDEO(4),

  /** 录音 */
  @EnumItem(ordinal = 5) RECORDING(5),

  /** 复印件图片 */
  @EnumItem(ordinal = 6) COPYFILE_IMAGE(6),

  /** 翻拍图片 */
  @EnumItem(ordinal = 7) REMAKE_IMAGE(7),

  /** 处理过的扫描件 */
  @EnumItem(ordinal = 8) PROCESSED_SCANNED_IMAGE(8),

  /** 处理过的图片 */
  @EnumItem(ordinal = 9) PROCESSED_IMAGE(9),

  /** 处理过的视频 */
  @EnumItem(ordinal = 10) PROCESSED_VIDEO(10),

  /** 处理过的音频 */
  @EnumItem(ordinal = 11) PROCESSED_AUDIO(11);

  override val value: Int = v

  companion object {
    @JvmStatic fun findVal(v: Int?) = CertContentTyping.entries.find { it.value == v }

    @JvmStatic operator fun get(v: Int?) = findVal(v)
  }
}
