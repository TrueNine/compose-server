package net.yan100.compose.core.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Transient

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
      "不推荐直接调用此方法，应使用 ksp 或其他工具进行生成，然后禁用警告",
      ReplaceWith(
        "DelegateGetSetLateinitvarValue<T>()",
        "net.yan100.compose.core.domain.DelegateGetSetLateinitvarValue"
      ),
      level = DeprecationLevel.ERROR
    )
    protected fun <T> Companion.late() = DelegateGetSetLateinitvarValue<T>()
  }
}
