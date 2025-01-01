package net.yan100.compose.client.domain

import net.yan100.compose.client.TsTypeDefine
import net.yan100.compose.client.domain.entries.TsName
import net.yan100.compose.meta.client.ClientType

/**
 * typescript 作用域定义
 * @param name 作用域名称
 * @param scopeQuota 作用域环绕修饰符
 * @param modifier 作用域特定前缀修饰符
 */
sealed class TsScope<T : TsScope<T>>(
  open val name: TsName,
  open val meta: ClientType,
  open val scopeQuota: TsScopeQuota = TsScopeQuota.BLANK,
  open val modifier: TsTypeModifier = TsTypeModifier.None
) : TsTypeDefine<TsScope<T>> {
  @Suppress("UNCHECKED_CAST")
  override fun fillGenerics(usedGenerics: List<TsGeneric>): T {
    if (!isRequireUseGeneric) return this as T
    return when (this) {
      is TypeAlias -> copy(usedGenerics = usedGenerics) as T
      is TypeVal -> copy(definition = definition.fillGenerics(usedGenerics)) as T
      else -> this as T
    }
  }

  @Suppress("UNCHECKED_CAST")
  override fun fillGenerics(vararg generic: TsGeneric): T {
    if (generic.isEmpty()) return this as T
    return fillGenerics(generic.toList())
  }

  override val isRequireUseGeneric: Boolean
    get() {
      return when (this) {
        is Class -> TODO()
        is Enum -> false
        is Interface -> superTypes.any { it.isRequireUseGeneric } || properties.any { it.isRequireUseGeneric }
        is TypeAlias -> usedGenerics.any { it is TsGeneric.UnUsed }
        is TypeVal -> definition.isRequireUseGeneric
      }
    }

  override val isBasic: Boolean
    get() = when (this) {
      is Enum -> true
      is TypeVal -> definition.isBasic
      is TypeAlias -> aliasFor.isBasic && usedGenerics.all { it.isBasic }
      is Interface -> superTypes.all { it.isBasic } && properties.all { it.isBasic }
      is Class -> error("Class is not supported")
    }

  fun toTsTypeVal(): TsTypeVal<*> {
    return when (this) {
      is Interface -> TsTypeVal.Ref(typeName = name)
      is Class -> TsTypeVal.Ref(typeName = name)
      is Enum -> TsTypeVal.Ref(typeName = name)
      is TypeAlias -> TsTypeVal.Ref(typeName = name)
      is TypeVal -> definition
    }
  }

  data class TypeAlias(
    override val name: TsName,
    val aliasFor: TsTypeVal<*>,
    override val meta: ClientType,
    val generics: List<TsGeneric.Defined> = emptyList(),
    val usedGenerics: List<TsGeneric> = emptyList()
  ) : TsScope<TypeAlias>(
    name = name,
    meta = meta,
    scopeQuota = TsScopeQuota.BLANK,
    modifier = TsTypeModifier.Type
  )

  data class TypeVal(
    val definition: TsTypeVal<*>,
    override val meta: ClientType
  ) : TsScope<TypeVal>(
    name = TsName.Anonymous,
    scopeQuota = TsScopeQuota.BLANK,
    meta = meta,
    modifier = TsTypeModifier.None
  )

  /**
   * 接口定义作用域
   * @param superTypes 所继承的父定义，不合理 应当被抹除
   */
  data class Interface(
    override val name: TsName,
    override val meta: ClientType,
    val generics: List<TsGeneric.Defined> = emptyList(),
    val superTypes: List<TsTypeVal<*>> = emptyList(),
    val properties: List<TsUseVal.Prop> = emptyList()
  ) : TsScope<Interface>(
    name = name,
    meta = meta,
    modifier = TsTypeModifier.Interface,
    scopeQuota = TsScopeQuota.OBJECT
  )

  /**
   * 枚举定义
   */
  data class Enum(
    override val name: TsName,
    override val meta: ClientType,
    val constants: Map<String, Comparable<*>>,
  ) : TsScope<Enum>(
    name = name,
    meta = meta,
    modifier = TsTypeModifier.Enum,
    scopeQuota = TsScopeQuota.OBJECT
  )

  /**
   * 类定义
   */
  data class Class(
    override val name: TsName,
    override val meta: ClientType,
    val superTypes: List<TsTypeVal.Ref>,
    // TODO 定义其他类的属性
  ) : TsScope<Class>(
    name = name,
    modifier = TsTypeModifier.Class,
    meta = meta,
    scopeQuota = TsScopeQuota.OBJECT
  )
}
