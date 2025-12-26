package io.github.truenine.composeserver.rds.jimmerextpostgres

import org.babyfish.jimmer.sql.kt.ast.expression.*

/**
 * ## Raw substr
 *
 * @param start start position, starting from 1
 * @param end end position
 */
fun KNullablePropExpression<String>.substr(start: Int?, end: Int?): KNonNullExpression<String> {
  return sql(String::class, "substr(%e,${start ?: "null"},${end ?: "null"})") { expression(this@substr) }
}
