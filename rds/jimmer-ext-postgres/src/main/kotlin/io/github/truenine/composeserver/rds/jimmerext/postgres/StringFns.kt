package io.github.truenine.composeserver.rds.jimmerext.postgres

import org.babyfish.jimmer.sql.kt.ast.expression.KNonNullExpression
import org.babyfish.jimmer.sql.kt.ast.expression.KNullablePropExpression
import org.babyfish.jimmer.sql.kt.ast.expression.sql
import org.babyfish.jimmer.sql.kt.ast.query.KMutableRootQuery

/**
 * ## 原始 substr
 *
 * @param start 起始位置 从 1 开始
 * @param end 结束位置
 */
fun KNullablePropExpression<String>.substr(start: Int?, end: Int?): KNonNullExpression<String> {
  return sql(String::class, "substr(%e,${start ?: "null"},${end ?: "null"})") { expression(this@substr) }
}

/**
 * ## 原始 substr
 *
 * @param str 原始字符串
 * @param start 起始位置 从 1 开始
 * @param end 结束位置
 */
fun <E : Any> KMutableRootQuery<E>.substr(str: String?, start: Int?, end: Int?): KNonNullExpression<String> {
  return sql(String::class, "substr($str,${start ?: "null"},${end ?: "null"})")
}
