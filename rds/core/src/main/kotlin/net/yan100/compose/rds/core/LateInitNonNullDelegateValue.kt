package net.yan100.compose.rds.core

import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
class LateInitNonNullDelegateValue<T> {
  private var v: Any? = null
  operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
    if (null == thisRef) throw NullPointerException("thisRef is null")
    v = value as Any
  }

  operator fun getValue(thisRef: Any?, property: KProperty<*>): T = v as T
}
