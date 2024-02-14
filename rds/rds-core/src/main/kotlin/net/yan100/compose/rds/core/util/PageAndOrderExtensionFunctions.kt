package net.yan100.compose.rds.core.util

import net.yan100.compose.core.lang.nonText
import net.yan100.compose.core.lang.snakeCaseToCamelCase
import org.springframework.data.domain.Sort

fun querydslOrderBy(orderFn: (it: MutableList<Sort.Order>) -> Unit): MutableList<Sort.Order> {
    val i = mutableListOf<Sort.Order>()
    orderFn(i)
    return i
}

fun MutableList<Sort.Order>.querydslOrderBy(propertyName: String, desc: Boolean? = null): MutableList<Sort.Order> {
    if (propertyName.nonText()) return this
    val pName = propertyName.snakeCaseToCamelCase
    if (desc != null) this += if (desc) Sort.Order.desc(pName) else Sort.Order.asc(pName)
    return this
}

fun MutableList<Sort.Order>.asQuerySort(): Sort {
    return Sort.by(this)
}


