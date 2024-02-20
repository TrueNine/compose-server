package net.yan100.compose.core.lang

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.annotation.Nullable
import net.yan100.compose.core.alias.decimal
import java.io.Serial
import java.io.Serializable
import java.math.BigDecimal

/**
 * 地址定位模型
 *
 * @author T_teng
 * @since 2023-04-06
 */
@Schema(title = "位置坐标")
class WGS84() : Serializable {
    constructor(x: BigDecimal?, y: BigDecimal?) : this() {
        this.x = x
        this.y = y
    }

    @Nullable
    var x: decimal? = null

    @Nullable
    var y: decimal? = null

    companion object {
        @Serial
        private val serialVersionUID = 1L
    }
}
