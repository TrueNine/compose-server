package net.yan100.compose.client.domain.entries

import net.yan100.compose.client.unwrapGenericName

sealed class TsName {
  fun isBasic(): Boolean {
    return when (this) {
      Anonymous,
      is Generic,
      is Name -> true

      is As,
      is PathName -> false
    }
  }

  /**
   * 将名称重定向到一个新的名称
   */
  open fun redirect(supportAs: Boolean = true): TsName {
    return when (this) {
      is Name -> if (supportAs) As(name, "${name}${name}")
      else Name("${name}${name}")

      is PathName -> PathName("${path}${name}", path)
      is Generic -> Generic("${name}${name}")
      is As -> As(name, "${asName}${asName}")
      Anonymous -> Anonymous
    }
  }

  /**
   * 匿名
   *
   * 不常用，用以表示当前无需名称的匿名类型
   *
   * ```typescript
   * interface A {
   *   (this: A, b: number) => void
   * }
   * ```
   */
  data object Anonymous : TsName() {
    override fun toString(): String = ""
  }

  /**
   * 被转换的名称
   *
   * 此种名称通常用于 as 语句
   * ```typescript
   * import {a as b) from './a'
   *
   * export {e as d}
   * ```
   */
  data class As(
    val name: String,
    val asName: String
  ) : TsName() {
    override fun toString(): String = asName
  }

  /**
   * 无需转换的命名
   *
   * 通常用于表达 typescript 或者 javascript 所支持的全局定义
   * ```typescript
   * Promise
   * File
   * Window
   * Document
   * Form
   * ```
   */
  data class Name(
    val name: String
  ) : TsName() {
    override fun toString(): String = name.unwrapGenericName()
  }

  /**
   * 泛型名称
   * ```typescript
   * <T>
   * ```
   */
  data class Generic(
    val name: String
  ) : TsName() {
    override fun toString(): String = name.unwrapGenericName()
    fun toName(): Name {
      return Name(toString())
    }
  }

  /**
   * 需要路径支持的名称
   *
   * 这通常用于 import 其他外部文件内声明的 ts 文件
   * @param name 命名
   * @param path 导入路径
   */
  data class PathName(
    val name: String,
    val path: String = ""
  ) : TsName() {
    override fun toString(): String = "${if (path.isNotEmpty()) "$path/" else ""}${name}"
  }
}
