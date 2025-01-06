package net.yan100.compose.client.domain

import net.yan100.compose.client.TsTypeDefine
import net.yan100.compose.client.domain.TsUseVal.Parameter
import net.yan100.compose.client.domain.TsUseVal.Return
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
sealed class TsTypeVal<T : TsTypeVal<T>> : TsTypeDefine<T> {
  @Suppress("UNCHECKED_CAST")
  override fun fillGenerics(usedGenerics: List<TsGeneric>): T {
    if (usedGenerics.isEmpty() || !isRequireUseGeneric) return this as T
    return when (this) {
      is Array -> copy(usedGeneric = usedGenerics.first()) as T
      is Generic -> copy(generic = usedGenerics.first()) as T
      is Promise -> copy(usedGeneric = usedGenerics.first()) as T
      is Ref -> copy(usedGenerics = usedGenerics) as T
      is Record -> copy(keyUsedGeneric = usedGenerics[0], valueUsedGeneric = usedGenerics[1]) as T
      is Object -> copy(
        elements = usedGenerics.mapIndexed { i, it ->
          val r = elements.getOrNull(i)
          if (r != null && r.isRequireUseGeneric) r.fillGenerics(it)
          else r
        }.filterNotNull()
      ) as T

      is Union,
      is Tuple,
      is Function,
      is TypeConstant -> this as T

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
      is Bigint -> this as T
    }
  }

  override val isRequireUseGeneric: kotlin.Boolean
    get() {
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

        is Object -> elements.any { it.isRequireUseGeneric }
        is Function -> params.any { it.isRequireUseGeneric } || returns.isRequireUseGeneric
        is Array -> usedGeneric.isRequireUseGeneric
        is Generic -> generic.isRequireUseGeneric
        is Promise -> usedGeneric.isRequireUseGeneric
        is Record -> keyUsedGeneric.isRequireUseGeneric || valueUsedGeneric.isRequireUseGeneric
        is Tuple -> elements.any { it.isRequireUseGeneric }
        is TypeConstant -> element.isRequireUseGeneric
        is Ref -> usedGenerics.any { it.isRequireUseGeneric }
        is Union -> joinTypes.any { it.isRequireUseGeneric }
      }
    }

  override val isBasic: kotlin.Boolean
    get() =
      when (this) {
        is Ref -> typeName.isBasic() && usedGenerics.all { it.isBasic }
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
        is EmptyObject -> true

        is Generic -> generic.isBasic
        is Array -> usedGeneric.isBasic
        is Union -> joinTypes.all { it.isBasic }
        is Function -> params.all { it.isBasic } && returns.isBasic
        is Object -> elements.all { it.isBasic }
        is Promise -> usedGeneric.isBasic
        is Record -> keyUsedGeneric.isBasic && valueUsedGeneric.isBasic
        is Tuple -> elements.all { it.isBasic }
        is TypeConstant -> element.isBasic
      }

  data class Tuple(
    val elements: List<TsTypeVal<*>>
  ) : TsTypeVal<Tuple>() {
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
  ) : TsTypeVal<Generic>() {
    override fun toString(): kotlin.String = generic.toString()
  }

  /**
   * 函数定义
   *
   * ```typescript
   * interface Foo {
   *   bar: (param: string) => void
   * }
   * ```
   */
  data class Function(
    val params: List<Parameter> = emptyList(),
    val returns: Return = Return(Void)
  ) : TsTypeVal<Function>() {
    override fun toString(): kotlin.String {
      return "(${TsScopeQuota.BRACKETS.left}${params.joinToString(", ") { it.toString() }}${TsScopeQuota.BRACKETS.right}) => $returns"
    }
  }

  /**
   * 联合类型
   * ```typescript
   * type foo = number | string | boolean | Array<string>
   * ```
   */
  data class Union(
    val joinTypes: List<TsTypeVal<*>>
  ) : TsTypeVal<Union>() {
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
    val elements: List<TsUseVal.Prop> = emptyList()
  ) : TsTypeVal<Object>() {
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
    val element: TsTypeVal<*>
  ) : TsTypeVal<TypeConstant>() {
    override fun toString(): kotlin.String = "$element as const"
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
  data class Ref(
    val typeName: TsName,
    val usedGenerics: List<TsGeneric> = emptyList()
  ) : TsTypeVal<Ref>() {
    override fun toString(): kotlin.String = when (usedGenerics.size) {
      0 -> typeName.toVariableName()
      else -> "${typeName.toVariableName()}<${usedGenerics.joinToString(", ") { it.toString() }}>"
    }
  }

  data object Number : TsTypeVal<Number>() {
    override fun toString(): kotlin.String = "number"
  }

  data object String : TsTypeVal<String>() {
    override fun toString(): kotlin.String = "string"
  }

  data object Bigint : TsTypeVal<Bigint>() {
    override fun toString(): kotlin.String = "bigint"
  }

  data object Boolean : TsTypeVal<Boolean>() {
    override fun toString(): kotlin.String = "boolean"
  }

  data object Symbol : TsTypeVal<Symbol>() {
    override fun toString(): kotlin.String = "symbol"
  }

  /**
   * 较为常用的一个 工具类型 `Record<K, V>`
   * 可将其处理为 `{[key: K]: V}`，一般用于描述 kotlin 中的 `Map<K,V>`
   */
  data class Record(
    val keyUsedGeneric: TsGeneric,
    val valueUsedGeneric: TsGeneric
  ) : TsTypeVal<Record>() {
    override fun toString(): kotlin.String = "Record<$keyUsedGeneric, $valueUsedGeneric>"
  }

  /**
   * 异步函数定义
   */
  data class Promise(
    val usedGeneric: TsGeneric
  ) : TsTypeVal<Promise>() {
    override fun toString(): kotlin.String = "Promise<$usedGeneric>"
  }

  data class Array(
    val usedGeneric: TsGeneric
  ) : TsTypeVal<Array>() {
    override fun toString() = "Array<$usedGeneric>"
  }

  /**
   * 在大多情况下，不推荐直接使用 `any`
   */
  data object Any : TsTypeVal<Any>() {
    override fun toString(): kotlin.String = "any"
  }


  data object Unknown : TsTypeVal<Unknown>() {
    override fun toString(): kotlin.String = "unknown"
  }

  data object Null : TsTypeVal<Null>() {
    override fun toString(): kotlin.String = "null"
  }

  /**
   * `undefined` 仅为类型定义，对应的值类型应当是 `void 0` 操作符
   */
  data object Undefined : TsTypeVal<Undefined>() {
    override fun toString(): kotlin.String = "undefined"
  }

  data object Never : TsTypeVal<Never>() {
    override fun toString(): kotlin.String = "never"
  }

  data object EmptyObject : TsTypeVal<EmptyObject>() {
    override fun toString(): kotlin.String = "object"
  }

  /**
   * 独特于 function 的返回值，不应当出现于其他场合
   */
  data object Void : TsTypeVal<Void>() {
    override fun toString(): kotlin.String = "void"
  }

  companion object {
    fun promiseFunction(
      params: List<Parameter> = emptyList(),
      returns: TsGeneric = TsGeneric.Used(Void)
    ) = Function(
      params = params,
      returns = Return(
        Promise(returns)
      )
    )
  }
}
