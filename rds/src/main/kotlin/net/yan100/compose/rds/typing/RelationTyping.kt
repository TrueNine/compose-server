package net.yan100.compose.rds.typing

import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema
import net.yan100.compose.core.lang.IntTyping

@Schema(title = "关系类型")
enum class RelationTyping(
    private val v: Int
) : IntTyping {
    @Schema(title = "无")
    NONE(0),

    @Schema(title = "受害者")
    VICTION(1),

    @Schema(title = "帮凶")
    PARTICIPATOR(2),

    @Schema(title = "见证人")
    WITNESS(3),

    @Schema(title = "其他")
    OTHER(9999);

    @JsonValue
    override val value: Int = v

    companion object {
        fun findVal(v: Int?) = entries.find { it.v == v }
    }
}
