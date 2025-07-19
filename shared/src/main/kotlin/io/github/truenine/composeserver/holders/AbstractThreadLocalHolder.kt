package io.github.truenine.composeserver.holders

import java.io.Closeable
import java.util.*
import kotlin.reflect.KClass
import org.springframework.core.NamedInheritableThreadLocal

abstract class AbstractThreadLocalHolder<T>(nameId: KClass<*>? = null, defaultValue: T? = null) : Closeable {
  private val holder by lazy {
    val name = nameId?.qualifiedName ?: this::class.qualifiedName ?: UUID.randomUUID().toString()
    NamedInheritableThreadLocal<T>(name)
  }

  init {
    if (defaultValue != null) holder.set(defaultValue)
  }

  override fun close() = holder.remove()

  var content: T
    get() = holder.get()
    set(value) = holder.set(value)

  open fun get(): T = holder.get()

  open fun set(value: T) = holder.set(value)

  operator fun component1(): T = holder.get()

  operator fun plusAssign(value: T) = holder.set(value)
}
