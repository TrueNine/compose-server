package net.yan100.compose.core.domain

import kotlin.properties.Delegates

/**
 * ## by late function scope
 *
 * @author TrueNine
 * @since 2024-10-01
 */
@Deprecated(
  "use Delegates.notNull()",
  ReplaceWith(
    "Delegates.notNull()"
  ),
  level = DeprecationLevel.ERROR
)
interface AbstractLazyInitScope {
  companion object {
    @JvmStatic
    @Suppress("DEPRECATION_ERROR")
    @Deprecated(
      "use Delegates.notNull()",
      ReplaceWith(
        "Delegates.notNull()"
      ),
      level = DeprecationLevel.ERROR
    )
    fun <T : Any> Companion.late() = Delegates.notNull<T>()
  }
}
