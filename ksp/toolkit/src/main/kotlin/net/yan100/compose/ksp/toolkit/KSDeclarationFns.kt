package net.yan100.compose.ksp.toolkit

import com.google.devtools.ksp.findActualType
import com.google.devtools.ksp.symbol.*
import kotlin.reflect.KClass

/**
 * `simpleName.asString()`
 *
 * @see KSDeclaration.simpleName
 * @see KSName.asString
 */
val KSDeclaration.simpleNameAsString: String
  get() = simpleName.asString()

/**
 * # 一般用此类表示 包名 + 类名
 * `qualifiedName.asString()`
 *
 * @see KSDeclaration.qualifiedName
 * @see KSName.asString
 */
val KSDeclaration.qualifiedNameAsString: String?
  get() {
    if (this.parentDeclaration != null) {
      val name = parentDeclaration?.qualifiedNameAsString!!
      if (this is KSTypeParameter) {
        this.bounds.map { it }
        return "<${name}::[${this.variance.label}] ${simpleNameAsString}>"
      }
      return "${name}\$${simpleNameAsString}"
    }
    return qualifiedName?.asString()
  }

/**
 * `packageName.asString()`
 *
 * @see KSDeclaration.packageName
 * @see KSName.asString
 */
val KSDeclaration.packageNameAsString: String
  get() = packageName.asString()

/**
 * `simpleName.getShortName()`
 *
 * @see KSDeclaration.simpleName
 * @see KSName.getShortName
 */
val KSDeclaration.simpleNameGetShortNameStr: String
  get() = simpleName.getShortName()

/** 其真实指向的 [KSDeclaration] */
val KSDeclaration.realDeclaration: KSDeclaration
  get() {
    val r =
      when (this) {
        is KSPropertyDeclaration -> this.type.fastResolve().declaration
        else -> this
      }
    return when (r) {
      is KSTypeAlias -> r.findActualType()
      else -> r
    }
  }

/** 该定义是否为基础类型 */
fun KSDeclaration.isBasicType(): Boolean {
  return when (realDeclaration.qualifiedNameAsString) {
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

/** 判断是否为指定类型 */
fun KSDeclaration.isKClass(kClass: KClass<*>): Boolean {
  return isKClassQualifiedName(kClass.qualifiedName!!)
}

/** 判断是否为指定类型 */
fun KSDeclaration.isKClassQualifiedName(qualifiedName: String): Boolean {
  require(qualifiedName.isBlank()) {
    "class $qualifiedName qName be null,qName ${qualifiedName}}"
  }
  return qualifiedNameAsString == qualifiedName.trim()
}

/** ## 获取真实类型的助注解 */
val KSDeclaration.actualAnnotationClassDeclarations:
  Sequence<KSClassDeclaration>
  get() {
    val ats = annotations.map { it.annotationType.fastResolve().declaration }
    return ats.map { d ->
      if (d is KSTypeAlias) {
        d.findActualType()
      } else d as KSClassDeclaration
    }
  }

/** 根据当前定义输出一段调试信息 */
fun KSDeclaration.debugInfo(): String = buildString {
  val annotationInfos =
    actualAnnotationClassDeclarations
      .map {
        val name = it.qualifiedNameAsString
        buildString { appendLine("Name: $name") }
      }
      .toList()

  appendLine("## Compose KSP DEBUG INFO START ============")
  appendLine("\n### DECLARATION\n")
  appendLine("- OriginType: `${origin.name}`")
  appendLine(
    "- location: `${(if (location is FileLocation) (location as FileLocation).filePath else "")}`"
  )
  appendLine(
    "- lineNumber: `${(if (location is FileLocation) (location as FileLocation).lineNumber else 0)}`"
  )
  appendLine("- isActual: `${isActual}`")
  appendLine("- isExpect: `${isExpect}`")
  appendLine("- TypeParameters Names: `${typeParameters.map { it.name }}`")
  if (modifiers.isNotEmpty())
    appendLine("- Modifiers: `${modifiers.joinToString { it.name }}`")

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
