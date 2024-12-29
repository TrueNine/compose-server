package net.yan100.compose.client.domain

import net.yan100.compose.client.domain.entries.TsName
import net.yan100.compose.meta.client.ClientType

/**
 * typescript 作用域定义
 * @param name 作用域名称
 * @param scopeQuota 作用域环绕修饰符
 * @param modifier 作用域特定前缀修饰符
 */
sealed class TsScope(
  open val name: TsName,
  open val meta: ClientType? = null,
  open val scopeQuota: TsScopeQuota = TsScopeQuota.BLANK,
  open val modifier: TsTypeModifier = TsTypeModifier.None
) {
  fun isBasic(): Boolean {
    return when (this) {
      is Enum -> true
      is TypeVal -> definition.isBasic()
      is TypeAlias -> aliasFor.isBasic() && usedGenerics.all { it.isBasic() }
      is Interface -> superTypes.all { it.isBasic() } && properties.all { it.defined.isBasic() }
      is Class -> error("Class is not supported")
    }
  }

  fun toTsTypeVal(): TsTypeVal {
    return when (this) {
      is Interface -> TsTypeVal.TypeDef(typeName = name)
      is Class -> TsTypeVal.TypeDef(typeName = name)
      is Enum -> TsTypeVal.TypeDef(typeName = name)
      is TypeAlias -> TsTypeVal.TypeDef(typeName = name)
      is TypeVal -> definition
    }
  }

  data class TypeAlias(
    override val name: TsName,
    val aliasFor: TsTypeVal,
    override val meta: ClientType? = null,
    val generics: List<TsGeneric.Defined> = emptyList(),
    val usedGenerics: List<TsGeneric> = emptyList()
  ) : TsScope(
    name = name,
    meta = meta,
    scopeQuota = TsScopeQuota.BLANK,
    modifier = TsTypeModifier.Type
  )

  data class TypeVal(
    val definition: TsTypeVal,
    override val meta: ClientType? = null
  ) : TsScope(
    name = TsName.Anonymous,
    scopeQuota = TsScopeQuota.BLANK,
    meta = meta,
    modifier = TsTypeModifier.None
  )

  /**
   * 接口定义作用域
   * @param superTypes 所继承的父定义，不合理的应当被抹除
   */
  data class Interface(
    override val name: TsName,
    override val meta: ClientType? = null,
    val generics: List<TsGeneric.Defined> = emptyList(),
    val superTypes: List<TsTypeVal> = emptyList(),
    val properties: List<TsTypeProperty> = emptyList()
  ) : TsScope(
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
    override val meta: ClientType? = null,
    val constants: Map<String, Comparable<*>>,
  ) : TsScope(
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
    override val meta: ClientType? = null,
    val superTypes: List<TsTypeVal.TypeDef>,
    // TODO 定义其他类的属性
  ) : TsScope(
    name = name,
    modifier = TsTypeModifier.Class,
    meta = meta,
    scopeQuota = TsScopeQuota.OBJECT
  )
}
