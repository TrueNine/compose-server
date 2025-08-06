package io.github.truenine.composeserver.rds.jimmerextpostgres

import org.babyfish.jimmer.sql.kt.ast.expression.KNonNullExpression
import org.babyfish.jimmer.sql.kt.ast.expression.KNullablePropExpression
import org.babyfish.jimmer.sql.kt.ast.expression.sql

/**
 * ## 原始 substr
 *
 * @param start 起始位置 从 1 开始
 * @param end 结束位置
 */
fun KNullablePropExpression<String>.substr(start: Int?, end: Int?): KNonNullExpression<String> {
  return sql(String::class, "substr(%e,${start ?: "null"},${end ?: "null"})") { expression(this@substr) }
}
