package net.yan100.compose.client.domain

import net.yan100.compose.client.domain.entries.TsName
import net.yan100.compose.client.toVariableName

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
  fun isRequireUseGeneric(): kotlin.Boolean {
    return when (this) {
      is Any,
      is Boolean,
      is EmptyObject,
      is Never,
      is Null,
      is Number,
      is String,
      is Symbol,
      is Undefined,
      is Unknown,
      is Void,
      is Bigint -> false

      is Object -> elements.any { it.isRequireUseGeneric() }
      is AnonymousFunction -> params.any { it.isRequireUseGeneric() } || returnType.isRequireUseGeneric()
      is Array -> usedGeneric.isRequireUseGeneric()
      is Generic -> generic.isRequireUseGeneric()
      is Promise -> usedGeneric.isRequireUseGeneric()
      is Record -> keyUsedGeneric.isRequireUseGeneric() || valueUsedGeneric.isRequireUseGeneric()
      is Tuple -> elements.any { it.isRequireUseGeneric() }
      is TypeConstant -> element.isRequireUseGeneric()
      is TypeDef -> usedGenerics.any { it.isRequireUseGeneric() }
      is Union -> joinTypes.any { it.isRequireUseGeneric() }
    }
  }

  data class Tuple(
    val elements: List<TsTypeProperty>
  ) : TsTypeVal() {
    override fun toString(): kotlin.String {
      return if (elements.isEmpty()) Array(TsGeneric.Used(Unknown, 0)).toString()
      else elements.joinToString(
        ", ",
        prefix = "[",
        postfix = "]"
      ) { it.toString() }
    }
  }

  data class Generic(
    val generic: TsGeneric
  ) : TsTypeVal() {
    override fun toString(): kotlin.String = generic.toString()
  }

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
  ) : TsTypeVal() {
    override fun toString(): kotlin.String {
      return "${TsScopeQuota.BRACKETS.left}${params.joinToString(", ") { it.toString() }}${TsScopeQuota.BRACKETS.right} => $returnType"
    }
  }

  /**
   * 联合类型
   * ```typescript
   * type foo = number | string | boolean | Array<string>
   * ```
   */
  data class Union(
    val joinTypes: List<TsTypeVal>
  ) : TsTypeVal() {
    override fun toString(): kotlin.String = joinTypes.joinToString(" | ") { it.toString() }
  }

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
    val elements: List<TsTypeProperty> = emptyList()
  ) : TsTypeVal() {
    override fun toString(): kotlin.String {
      return if (elements.isEmpty()) EmptyObject.toString()
      else elements.joinToString(
        ", ",
        prefix = TsScopeQuota.OBJECT.left,
        postfix = TsScopeQuota.OBJECT.right
      ) { it.toString() }
    }
  }

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
  ) : TsTypeVal() {
    override fun toString(): kotlin.String {
      return "$element as const"
    }
  }

  /**
   * 最直接的 type 类型
   *
   * ```typescript
   * Foo
   * Bar
   * Foo<T>
   * Bar<A,B,string>
   * ```
   * @param typeName 类型名称
   * @param usedGenerics 使用的泛型
   */
  data class TypeDef(
    val typeName: TsName,
    val usedGenerics: List<TsGeneric> = emptyList()
  ) : TsTypeVal() {
    override fun toString(): kotlin.String {
      return when (usedGenerics.size) {
        0 -> typeName.toVariableName()
        else -> "${typeName.toVariableName()}<${usedGenerics.joinToString(", ") { it.toString() }}>"
      }
    }
  }

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
  ) : TsTypeVal() {
    override fun toString(): kotlin.String {
      return "Record<$keyUsedGeneric, $valueUsedGeneric>"
    }
  }

  /**
   * 异步函数定义
   */
  data class Promise(
    val usedGeneric: TsGeneric
  ) : TsTypeVal() {
    override fun toString(): kotlin.String = "Promise<$usedGeneric>"
  }

  data class Array(
    val usedGeneric: TsGeneric
  ) : TsTypeVal() {
    override fun toString() = "Array<$usedGeneric>"
  }

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

  data object Never : TsTypeVal() {
    override fun toString(): kotlin.String = "never"
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

  fun isBasic(): kotlin.Boolean {
    return when (this) {
      is TypeDef -> typeName.isBasic() && usedGenerics.all { it.isBasic() }
      is Never,
      is Any,
      is String,
      is Unknown,
      is Void,
      is Boolean,
      is Bigint,
      is Number,
      is Symbol,
      is Null,
      is Undefined,
      is EmptyObject
        -> true

      is Generic -> generic.isBasic()
      is Array -> usedGeneric.isBasic()
      is Union -> joinTypes.all { it.isBasic() }
      is AnonymousFunction -> params.all { it.defined.isBasic() } && returnType.isBasic()
      is Object -> elements.all { it.defined.isBasic() }
      is Promise -> usedGeneric.isBasic()
      is Record -> keyUsedGeneric.isBasic() && valueUsedGeneric.isBasic()
      is Tuple -> elements.all { it.isBasic() }
      is TypeConstant -> element.isBasic()
    }
  }
}
