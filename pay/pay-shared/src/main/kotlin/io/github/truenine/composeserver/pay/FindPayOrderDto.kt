package io.github.truenine.composeserver.pay

import io.swagger.v3.oas.annotations.media.Schema

/** # Payment order query parameters */
data class FindPayOrderDto(
  /** ## Merchant order ID */
  @Schema(title = "Merchant order ID") var merchantOrderId: String? = null,
  /** ## Third-party order ID */
  @Schema(title = "Third-party order ID") var bizCode: String? = null,
)
