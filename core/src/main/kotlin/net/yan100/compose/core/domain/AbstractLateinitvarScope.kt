package net.yan100.compose.core.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Transient
import kotlin.properties.Delegates

/**
 * ## by late function scope
 *
 * @author TrueNine
 * @since 2024-10-01
 */
abstract class AbstractLateinitvarScope {
  companion object {
    @JsonIgnore
    @Transient
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
