package io.github.truenine.composeserver

import io.github.truenine.composeserver.enums.HttpStatus

/**
 * # New error response entity
 * > Compared to the previous version, this uses Kotlin data class design
 *
 * @param errorBy Error enumeration
 * @param code Error code
 * @param msg Error message
 * @param alt Error suggestion (what to do when encountering this error)
 * @param debugSerialTrace Error trace information
 * @author TrueNine
 * @since 2025-03-01
 */
data class ErrorResponseEntity
@JvmOverloads
constructor(
  val errorBy: HttpStatus = HttpStatus.UNKNOWN,
  val code: Int? = errorBy.code,
  val msg: String? = errorBy.message,
  val alt: String? = null,
  val debugSerialTrace: Any? = null,
)
