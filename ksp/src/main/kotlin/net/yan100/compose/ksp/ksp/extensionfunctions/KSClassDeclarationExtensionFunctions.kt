package net.yan100.compose.ksp.ksp.extensionfunctions

import com.google.devtools.ksp.symbol.KSClassDeclaration
import kotlin.reflect.KClass

fun KSClassDeclaration.isAssignableFromDeeply(other: KClass<*>, checkList: MutableSet<KSClassDeclaration> = mutableSetOf()): Boolean {
  if (!checkList.add(this)) return false
  if (asStarProjectedType().declaration.qualifiedName?.asString() == other.qualifiedName) return true
  for (superType in superTypes) {
    val superTypeDeclaration = superType.resolve().declaration as? KSClassDeclaration ?: continue
    if (superTypeDeclaration.isAssignableFromDeeply(other, checkList)) return true
  }
  return false
}
