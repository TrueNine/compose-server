package net.yan100.compose.core.domain

import io.swagger.v3.oas.annotations.media.Schema
import java.io.Serializable
import net.yan100.compose.core.decimal

/**
 * # 地址定位模型
 *
 * @author T_teng
 * @since 2023-04-06
 */
@Schema(title = "位置坐标")
class Coordinate
@JvmOverloads
constructor(var x: decimal? = null, var y: decimal? = null) : Serializable
