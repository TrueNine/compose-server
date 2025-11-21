package io.github.truenine.composeserver.ide.ideamcp.common

import kotlinx.serialization.Serializable

/** Error details. */
@Serializable
data class ErrorDetails(
  /** Error type. */
  val type: String,
  /** Error message. */
  val message: String,
  /** Suggested solutions. */
  val suggestions: List<String> = emptyList(),
)
