package net.yan100.compose.core.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import kotlin.properties.Delegates

/**
 * ## by late function scope
 *
 * @author TrueNine
 * @since 2024-10-01
 */
abstract class AbstractLazyInitScope {
  companion object {
    @JsonIgnore
    @JvmStatic
    @Suppress("DEPRECATION_ERROR")
    @Deprecated(
      "use Delegates.notNull()",
      ReplaceWith(
        "Delegates.notNull()"
      ),
      level = DeprecationLevel.ERROR
    )
    protected fun <T : Any> Companion.late() = Delegates.notNull<T>()
  }
}
