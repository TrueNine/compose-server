package net.yan100.compose.client.domain

import net.yan100.compose.client.domain.entries.TypescriptName

/**
 * typescript 作用域定义
 * @param name 作用域名称
 * @param scopeQuota 作用域环绕修饰符
 * @param modifier 作用域特定前缀修饰符
 */
sealed class TypescriptScope(
  open val name: TypescriptName.Name,
  open val scopeQuota: TypescriptScopeScopeQuota = TypescriptScopeScopeQuota.BLANK,
  open val modifier: TypescriptTypeModifier = TypescriptTypeModifier.None()
) {

  /**
   * 接口定义作用域
   * @param superTypes 所继承的父定义，不合理的应当被抹除
   */
  data class Interface(
    override val name: TypescriptName.Name,
    val generics: List<TypescriptGeneric.Defined>,
    val superTypes: List<TypescriptTypeValue>,
    val properties: List<TypescriptTypeProperty>
  ) : TypescriptScope(
    name = name,
    modifier = TypescriptTypeModifier.Interface(),
    scopeQuota = TypescriptScopeScopeQuota.OBJECT
  )

  /**
   * 枚举定义
   */
  data class Enum(
    override val name: TypescriptName.Name,
    val constants: Map<String, Comparable<*>>,
  ) : TypescriptScope(
    name = name,
    modifier = TypescriptTypeModifier.Enum(),
    scopeQuota = TypescriptScopeScopeQuota.OBJECT
  )

  /**
   * 类定义
   */
  data class Class(
    override val name: TypescriptName.Name,
    val superTypes: List<TypescriptTypeValue.TypeDef>,
    // TODO 定义其他类的属性
  ) : TypescriptScope(
    name = name,
    modifier = TypescriptTypeModifier.Class(),
    scopeQuota = TypescriptScopeScopeQuota.OBJECT
  )
}
