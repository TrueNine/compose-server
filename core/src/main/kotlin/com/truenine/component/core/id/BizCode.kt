package com.truenine.component.core.id

interface BizCode {
  fun nextCode(): Long
  fun nextCodeStr(): String
}
