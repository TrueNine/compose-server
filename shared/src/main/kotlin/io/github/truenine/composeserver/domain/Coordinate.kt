package io.github.truenine.composeserver.domain

import io.github.truenine.composeserver.decimal
import io.swagger.v3.oas.annotations.media.Schema
import java.io.Serializable

/**
 * # Address location model
 *
 * @author T_teng
 * @since 2023-04-06
 */
@Schema(title = "Location coordinates") class Coordinate @JvmOverloads constructor(var x: decimal? = null, var y: decimal? = null) : Serializable
