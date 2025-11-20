package io.github.truenine.composeserver

import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * ## Recursively obtain all fields of a class.
 *
 * @param endType Superclass at which to stop recursion
 * @return All fields on this class and its superclasses up to the endType (exclusive)
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
