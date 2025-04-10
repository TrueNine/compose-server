package net.yan100.compose.meta.client

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import net.yan100.compose.meta.types.Doc
import net.yan100.compose.meta.types.TypeKind
import net.yan100.compose.meta.types.TypeName

private val classResolveCache = ConcurrentHashMap<String, Class<*>?>()

/** 类型定义 */
data class ClientType(
  override val typeName: String,
  override val doc: Doc? = null,
  override val typeKind: TypeKind? = null,
  override val superTypes: List<ClientType> = emptyList(),
  override val builtin: Boolean? = null,

  /** 是否为别名 */
  val isAlias: Boolean? = null,

  /** 该别名对应的类型名 */
  val aliasForTypeName: String? = null,

  /** 该类的泛型列表，仅表示当前类需要填写多少泛型参数 */
  val arguments: List<String> = emptyList(),

  /** 所填写的泛型参数 */
  val usedGenerics: List<ClientUsedGeneric> = emptyList(),

  /** 该类具有的属性列表 */
  val properties: List<ClientProp> = emptyList(),

  /**
   * 枚举常量值列表
   *
   * 如果这是一个枚举类型
   */
  val enumConstants: Map<String, Int> = emptyMap(),

  /** 是否建议为 null，一般表现在 ReturnType */
  val nullable: Boolean? = null,
) : TypeName {
  companion object {
    fun none(): ClientType {
      return ClientType(typeName = "", nullable = true)
    }
  }

  private fun String.toTransientType(): ClientType {
    return ClientType(this, typeKind = TypeKind.TRANSIENT)
  }

  fun isAssignableFrom(otherQualifierName: String): Boolean {
    return when {
      typeName == otherQualifierName -> true
      isAlias == true -> {
        val aliasForTypeName = this.aliasForTypeName ?: return false
        val aliasForType = ClientType(aliasForTypeName)
        aliasForType.isAssignableFrom(otherQualifierName)
      }

      superTypes.isNotEmpty() -> {
        return superTypes.any { it.isAssignableFrom(otherQualifierName) }
      }

      else -> false
    }
  }

  fun isAssignableFrom(other: ClientType): Boolean =
    isAssignableFrom(other.typeName)

  fun resolveEnumConstants(): Map<String, Comparable<Nothing>> {
    val java = resolveJava() ?: return emptyMap()
    if (!java.isEnum) return emptyMap()
    val superEnum = resolveJava("net.yan100.compose.typing.AnyTyping")
    val isTyping = superEnum?.isAssignableFrom(java) == true
    return java.enumConstants
      .filterIsInstance<Enum<*>>()
      .map {
        if (isTyping) {
          it.name to java.getMethod("getValue").invoke(it) as Comparable<*>
        } else {
          it.name to it.ordinal
        }
      }
      .toMap()
  }

  private fun resolveJava(typeName: String): Class<*>? {
    return try {
      if (classResolveCache.containsKey(typeName))
        return classResolveCache[typeName]
      classResolveCache[typeName] =
        try {
          Class.forName(typeName)
        } catch (_: ClassNotFoundException) {
          null
        }
      classResolveCache[typeName]
    } catch (_: ClassNotFoundException) {
      null
    }
  }

  private fun resolveJava(): Class<*>? {
    return resolveJava(typeName)
  }

  fun resolveKotlin(): KClass<*>? {
    return resolveJava()?.kotlin
  }

  fun changeAllTypeToCopy(resolver: (ClientType) -> ClientType?): ClientType? {
    val r = resolver(this) ?: return null
    val properties =
      r.properties.mapNotNull {
        val eee = resolver(it.typeName.toTransientType())
        eee?.typeName?.let { n -> it.copy(typeName = n) }
      }
    return r.copy(
      properties = properties,
      superTypes = resolveAllSuperTypes(r.superTypes, resolver),
    )
  }

  private fun resolveAllSuperTypes(
    superTypes: List<ClientType>,
    newSuperTypeResolver: (ClientType) -> ClientType?,
  ): List<ClientType> {
    return superTypes.mapNotNull {
      val r = newSuperTypeResolver(it)
      r?.copy(
        superTypes = resolveAllSuperTypes(it.superTypes, newSuperTypeResolver)
      )
    }
  }
}
