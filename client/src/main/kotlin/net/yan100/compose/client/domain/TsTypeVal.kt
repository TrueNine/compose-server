package net.yan100.compose.client.domain

import net.yan100.compose.client.domain.entries.TsName

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
sealed class TsTypeVal {
  data class Tuple(
    val elements: List<TsTypeProperty>
  ) : TsTypeVal()

  /**
   * 匿名函数定义
   * ```typescript
   * interface Foo {
   *   bar: (param: string) => void
   * }
   * ```
   */
  data class AnonymousFunction(
    val params: List<TsTypeProperty>,
    val returnType: TsTypeVal
  ) : TsTypeVal()

  /**
   * 联合类型
   * ```typescript
   * type foo = number | string | boolean | Array<string>
   * ```
   */
  data class Union(
    val joinTypes: List<TsTypeVal>
  ) : TsTypeVal()

  /**
   * 表示 typescript 中的 object 对象
   * ```typescript
   * export type Foo = {bar1:string,bar2:number...}
   * ```
   *
   * 空 object 众多场合中不当被渲染为 `{}` 而是更精确的 `object`，因为 `{}` 会被识别为近似 `Record<string, unknown>` 的类型，进
   * 而导致传递任意结构，这可能不是 kotlin 的期望，或者转而使用 `Record`
   *
   * @param elements 对象元素
   * @see [TsTypeVal.EmptyObject]
   * @see [TsTypeVal.Record]
   */
  data class Object(
    val elements: List<TsTypeProperty>
  ) : TsTypeVal()

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
    val element: TsTypeVal
  ) : TsTypeVal()

  /**
   * 最直接的 type 类型
   * @param typeName 类型名称
   * @param usedGenerics 使用的泛型
   */
  data class TypeDef(
    val typeName: TsName,
    val usedGenerics: List<TsGeneric> = emptyList()
  ) : TsTypeVal()

  data object Number : TsTypeVal() {
    override fun toString(): kotlin.String = "number"
  }

  data object String : TsTypeVal() {
    override fun toString(): kotlin.String = "string"
  }

  data object Bigint : TsTypeVal() {
    override fun toString(): kotlin.String = "bigint"
  }

  data object Boolean : TsTypeVal() {
    override fun toString(): kotlin.String = "boolean"
  }

  data object Symbol : TsTypeVal() {
    override fun toString(): kotlin.String = "symbol"
  }

  /**
   * 较为常用的一个 工具类型 `Record<K, V>`
   * 可将其处理为 `{[key: K]: V}`，一般用于描述 kotlin 中的 `Map<K,V>`
   */
  data class Record(
    val keyUsedGeneric: TsGeneric,
    val valueUsedGeneric: TsGeneric
  ) : TsTypeVal()

  /**
   * 异步函数定义
   */
  data class Promise(
    val usedGeneric: TsGeneric
  ) : TsTypeVal()

  data class Array(
    val usedGeneric: TsGeneric
  ) : TsTypeVal()


  /**
   * 在大多情况下，不推荐直接使用 `any`
   */
  data object Any : TsTypeVal() {
    override fun toString(): kotlin.String = "any"
  }


  data object Unknown : TsTypeVal() {
    override fun toString(): kotlin.String = "unknown"
  }

  data object Null : TsTypeVal() {
    override fun toString(): kotlin.String = "null"
  }

  /**
   * `undefined` 仅为类型定义，对应的值类型应当是 `void 0` 操作符
   */
  data object Undefined : TsTypeVal() {
    override fun toString(): kotlin.String = "undefined"
  }

  data object EmptyObject : TsTypeVal() {
    override fun toString(): kotlin.String = "object"
  }

  /**
   * 独特于 function 的返回值，不应当出现于其他场合
   */
  data object Void : TsTypeVal() {
    override fun toString(): kotlin.String = "void"
  }
}
