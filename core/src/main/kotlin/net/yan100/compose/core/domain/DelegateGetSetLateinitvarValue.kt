package net.yan100.compose.core.domain

import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
class DelegateGetSetLateinitvarValue<T> @Deprecated(message = "不建议直接调用构造器直接实例化", level = DeprecationLevel.ERROR) constructor() {
  private var v: Any? = null
  operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
    if (null == thisRef) throw NullPointerException("lazy set value to be null")
    v = value
  }

  operator fun getValue(thisRef: Any?, property: KProperty<*>): T = v as T
}