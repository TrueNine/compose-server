package net.yan100.compose.rds

import net.yan100.compose.DisRule
import net.yan100.compose.int
import net.yan100.compose.rds.typing.DisTyping

fun DisRule.match(type: DisTyping, level: int): Boolean {
  return match(type.value, level)
}
