package net.yan100.compose.client.domain

import net.yan100.compose.client.domain.entries.TypescriptName

/**
 * typescript 的类型通常右值定义
 *
 * - () => function
 * - union | union | union
 * - constant as constant
 * - array[]
 * - [ tuple ]
 * - {}
 * - type type<generic...>
 */
sealed class TypescriptTypeValue {
  data class Tuple(
    val elements: List<TypescriptTypeProperty>
  ) : TypescriptTypeValue()

  /**
   * 匿名函数定义
   * ```typescript
   * interface Foo {
   *   bar: (param: string) => void
   * }
   * ```
   */
  data class AnonymousFunction(
    val params: List<TypescriptTypeProperty>,
    val returnType: TypescriptTypeValue
  )

  /**
   * 联合类型
   * ```typescript
   * type foo = number | string | boolean | Array<string>
   * ```
   */
  data class Union(
    val joinTypes: List<TypescriptTypeValue>
  ) : TypescriptTypeValue()

  /**
   * 对象定义，如果对象为 `{}` 大多场合下应当被处理为 `object`
   * @param elements 对象元素
   */
  data class Objects(
    val elements: List<TypescriptTypeProperty>
  ) : TypescriptTypeValue()

  /**
   * 此应当被转换为 `as const`，支持大多默认类型处理
   * ```typescript
   * type foo1 = 1 as const
   * type foo2 = "1" as const
   * type foo3 = true as const
   * type foo4 = [1, 2, 3] as const
   * ```
   */
  data class TypeConstant(
    val element: Any?
  ) : TypescriptTypeValue()

  /**
   * 最直接的 type 类型
   * @param typeName 类型名称
   * @param usedGenerics 使用的泛型
   */
  data class TypeDef(
    val typeName: TypescriptName,
    val usedGenerics: List<TypescriptGeneric>
  ) : TypescriptTypeValue()

  data object Number : TypescriptTypeValue()
  data object String : TypescriptTypeValue()
  data object Bigint : TypescriptTypeValue()
  data object Boolean : TypescriptTypeValue()
  data object Symbol : TypescriptTypeValue()

  /**
   * 空 Objects 不应当被渲染为 `{}` 而是更精确的 `object`
   */
  data object Object : TypescriptTypeValue()

  /**
   * 较为常用的一个 工具类型 `Record<K, V>`
   * 可将其处理为 `{[key: K]: V}`，一般用于描述 kotlin 中的 `Map<K,V>`
   */
  data class Record(
    val keyUsedGeneric: TypescriptGeneric,
    val valueUsedGeneric: TypescriptGeneric
  ) : TypescriptTypeValue()

  data class Array(
    val usedGeneric: TypescriptGeneric
  ) : TypescriptTypeValue()

  data object Any : TypescriptTypeValue()
  data object Unknown : TypescriptTypeValue()
  data object Null : TypescriptTypeValue()

  /**
   * `undefined` 仅为类型定义，对应的值类型应当是 `void 0` 操作符
   */
  data object Undefined : TypescriptTypeValue()

  /**
   * 独特于 function 的返回值，不应当出现于其他场合
   */
  data object Void : TypescriptTypeValue()
}
