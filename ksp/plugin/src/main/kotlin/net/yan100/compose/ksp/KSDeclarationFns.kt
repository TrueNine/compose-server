package net.yan100.compose.ksp

import com.google.devtools.ksp.findActualType
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSTypeAlias


val KSDeclaration.sName: String get() = simpleName.asString()
val KSDeclaration.qName: String? get() = qualifiedName?.asString()

/**
 * `simpleName.getShortName()`
 */
val KSDeclaration.shName: String get() = simpleName.getShortName()
val KSDeclaration.actualType: KSDeclaration
  get() {
    val resolved = if (this is KSTypeAlias) findActualType()
    else this
    return when (resolved) {
      is KSPropertyDeclaration -> resolved.type.resolve().declaration
      else -> resolved
    }
  }

fun KSDeclaration.isBasicType(): Boolean {
  return when (actualType.qName) {
    "java.lang.Character",
    "kotlin.Char",
    "java.lang.Integer",
    "kotlin.Int",
    "java.lang.Long",
    "kotlin.Long",
    "java.lang.Double",
    "kotlin.Double",
    "java.lang.Float",
    "kotlin.Float",
    "java.lang.Boolean",
    "kotlin.Boolean",
    "java.lang.Short",
    "kotlin.Short",
    "java.lang.Byte",
    "kotlin.Byte" -> true

    else -> false
  }
}
