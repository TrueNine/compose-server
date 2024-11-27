package net.yan100.compose.ksp

import com.google.devtools.ksp.findActualType
import com.google.devtools.ksp.symbol.FileLocation
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSTypeAlias
import net.yan100.compose.core.hasText
import kotlin.reflect.KClass


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

fun KSDeclaration.isType(clazz: KClass<*>): Boolean {
  val name = clazz.qualifiedName
  require(name.hasText()) { "class $clazz qName be null,qName ${clazz.qualifiedName}}" }
  return qName == name
}

fun KSDeclaration.debugInfo(): String = buildString {
  val annotationInfos = actualAnnotations.map {
    val name = it.qName
    buildString {
      appendLine("Name: $name")
    }
  }.toList()

  appendLine("## Compose KSP DEBUG INFO START ============")
  appendLine("\n### DECLARATION\n")
  appendLine("- OriginType: `${origin.name}`")
  appendLine("- location: `${(if (location is FileLocation) (location as FileLocation).filePath else "")}`")
  appendLine("- lineNumber: `${(if (location is FileLocation) (location as FileLocation).lineNumber else 0)}`")
  appendLine("- isActual: `${isActual}`")
  appendLine("- isExpect: `${isExpect}`")
  appendLine("- TypeParameters Names: `${typeParameters.map { it.name }}`")
  if (modifiers.isNotEmpty()) appendLine("- Modifiers: `${modifiers.joinToString { it.name }}`")

  appendLine("- packageName asString: `${packageName.asString()}`")
  appendLine("- packageName getShortName: ${packageName.getShortName()}")
  appendLine("- packageName getQualifier: ${packageName.getQualifier()}")
  appendLine("- simpleName asString: ${simpleName.asString()}")
  appendLine("- simpleName getShortName: ${simpleName.getShortName()}")
  appendLine("- simpleName getQualifier: ${simpleName.getQualifier()}")

  // ===
  docString?.also { doc ->
    appendLine("\n### DOC STRING\n")
    appendLine("```text\n${doc}\n```")
  }

  // ===
  if (annotationInfos.isNotEmpty()) {
    appendLine("\n### ANNOTATIONS\n")
    appendLine("```text")
    annotationInfos.forEach {
      appendLine(it)
      appendLine()
    }
    appendLine("```")
  }

  appendLine("Compose KSP DEBUG INFO END ============")
}
