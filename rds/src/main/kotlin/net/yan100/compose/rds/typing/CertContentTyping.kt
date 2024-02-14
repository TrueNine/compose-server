package net.yan100.compose.rds.typing

import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.core.lang.IntTyping

@Schema(title = "证件内容类型")
enum class CertContentTyping(
    private val v: Int
) : IntTyping {
    @Schema(title = "无要求")
    NONE(0),

    @Schema(title = "图片")
    IMAGE(1),

    @Schema(title = "扫描件图片")
    SCANNED_IMAGE(2),

    @Schema(title = "截图")
    SCREEN_SHOT(3),

    @Schema(title = "视频")
    VIDEO(4),

    @Schema(title = "录音")
    RECORDING(5),

    @Schema(title = "复印件图片")
    COPYFILE_IMAGE(6),

    @Schema(title = "翻拍图片")
    REMAKE_IMAGE(7),

    @Schema(title = "处理过的扫描件")
    PROCESSED_SCANNED_IMAGE(8),

    @Schema(title = "处理过的图片")
    PROCESSED_IMAGE(9),

    @Schema(title = "处理过的视频")
    PROCESSED_VIDEO(10),

    @Schema(title = "处理过的音频")
    PROCESSED_AUDIO(11), ;

    @JsonValue
    override val value: Int = v

    companion object {
        @JvmStatic
        fun findVal(v: Int?) = CertContentTyping.entries.find { it.value == v }
    }
}
