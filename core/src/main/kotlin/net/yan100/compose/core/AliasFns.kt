package net.yan100.compose.core

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * ## 判断当前 Id 类型是否不为空
 */
@OptIn(ExperimentalContracts::class)
fun Id?.isId(): Boolean {
  contract { returns(true) implies (this@isId != null) }
  return this != null && this.hasText() && this.matches(Regex("^[0-9]+$"))
}
