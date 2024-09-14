package net.yan100.compose.rds.core

import net.yan100.compose.core.DisRule
import net.yan100.compose.core.int
import net.yan100.compose.rds.core.typing.cert.DisTyping

fun DisRule.match(type: DisTyping, level: int): Boolean {
  return match(type.value, level)
}
