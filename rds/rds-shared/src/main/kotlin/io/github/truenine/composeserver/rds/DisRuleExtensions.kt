package io.github.truenine.composeserver.rds

import io.github.truenine.composeserver.domain.DisRule
import io.github.truenine.composeserver.int
import io.github.truenine.composeserver.rds.enums.DisTyping

fun DisRule.match(type: DisTyping, level: int): Boolean {
  return match(type.value, level)
}
