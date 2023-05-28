package net.yan100.compose.core.lang

import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * ## 递归获取一个类的所有属性
 * @param endType 结束的类型
 * @return 当前类以及所有到结束标记为止的 fields
 */
fun KClass<*>.recursionFields(endType: KClass<*> = Any::class): Array<out Field> {
  val selfFields = mutableListOf<Field>()
  var superClass: Class<*>? = this.java
  val endsWith = endType.java
  while (superClass != null) {
    selfFields += superClass.declaredFields
    superClass = superClass.superclass
    if (superClass == endsWith) break
  }
  return selfFields.toTypedArray()
}
