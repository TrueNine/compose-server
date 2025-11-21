package io.github.truenine.composeserver.ksp

import com.google.devtools.ksp.symbol.KSClassDeclaration
import kotlin.reflect.KClass

private fun KSClassDeclaration.internalIsAssignableFromDeeply(other: KClass<*>, checkList: MutableSet<KSClassDeclaration> = mutableSetOf()): Boolean {
  if (!checkList.add(this)) return false
  if (asStarProjectedType().declaration.qualifiedName?.asString() == other.qualifiedName) return true
  for (superType in superTypes) {
    val superTypeDeclaration = superType.fastResolve().declaration as? KSClassDeclaration ?: continue
    if (superTypeDeclaration.internalIsAssignableFromDeeply(other, checkList)) return true
  }
  return false
}

/**
 * Check whether the current class or its superclasses are compatible with the
 * specified [KClass]. This function recursively checks the current class and
 * its superclasses to determine compatibility. To avoid infinite recursion, a
 * check list is used to record classes that have already been visited.
 *
 * @param other The [KClass] to check for compatibility with the current class
 *   or its superclasses.
 * @return true if the current class or its superclasses are compatible with
 *   the specified KClass; false otherwise.
 */
fun KSClassDeclaration.isAssignableFromDeeply(other: KClass<*>): Boolean {
  return internalIsAssignableFromDeeply(other, mutableSetOf())
}
