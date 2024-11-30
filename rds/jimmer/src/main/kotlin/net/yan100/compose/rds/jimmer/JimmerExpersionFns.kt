package net.yan100.compose.rds.jimmer

import org.babyfish.jimmer.sql.ast.query.Order
import org.babyfish.jimmer.sql.kt.ast.expression.KExpression
import org.babyfish.jimmer.sql.kt.ast.expression.asc
import org.babyfish.jimmer.sql.kt.ast.expression.desc

infix fun KExpression<*>.desc(condition: Boolean): Order {
  return if (condition) this.desc() else asc()
}

infix fun KExpression<*>.`desc?`(condition: Boolean?): Order {
  return this desc (condition == true)
}
