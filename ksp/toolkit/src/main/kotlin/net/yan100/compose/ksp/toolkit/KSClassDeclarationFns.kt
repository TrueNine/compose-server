package net.yan100.compose.ksp.toolkit

import com.google.devtools.ksp.symbol.KSClassDeclaration
import kotlin.reflect.KClass

private fun KSClassDeclaration.internalIsAssignableFromDeeply(
  other: KClass<*>,
  checkList: MutableSet<KSClassDeclaration> = mutableSetOf(),
): Boolean {
  if (!checkList.add(this)) return false
  if (
    asStarProjectedType().declaration.qualifiedName?.asString() ==
      other.qualifiedName
  )
    return true
  for (superType in superTypes) {
    val superTypeDeclaration =
      superType.fastResolve().declaration as? KSClassDeclaration ?: continue
    if (superTypeDeclaration.internalIsAssignableFromDeeply(other, checkList))
      return true
  }
  return false
}

/**
 * 检查当前类或其超类是否与指定的 [KClass] 对象兼容。 这个函数会递归地检查当前类及其超类，以确定是否存在兼容性。
 * 为了避免无限递归，使用了一个检查列表来记录已经检查过的类。
 *
 * @param other 要检查的 [KClass] 对象，以确定它是否与当前类或其超类兼容。
 * @return 如果当前类或其超类与指定的KClass对象兼容，则返回true；否则返回false。
 */
fun KSClassDeclaration.isAssignableFromDeeply(other: KClass<*>): Boolean {
  return internalIsAssignableFromDeeply(other, mutableSetOf())
}
