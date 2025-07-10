package net.yan100.compose.rds

import net.yan100.compose.nonText
import net.yan100.compose.toPascalCase
import org.springframework.data.domain.Sort

fun MutableList<Sort.Order>.querydslOrderBy(propertyName: String, desc: Boolean? = null): MutableList<Sort.Order> {
  if (propertyName.nonText()) return this
  val pName = propertyName.toPascalCase(false)
  if (desc != null) this += if (desc) Sort.Order.desc(pName) else Sort.Order.asc(pName)
  return this
}

fun MutableList<Sort.Order>.asQuerySort(): Sort {
  return Sort.by(this)
}
