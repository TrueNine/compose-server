package io.github.truenine.composeserver

import io.github.truenine.composeserver.enums.PCB47
import io.github.truenine.composeserver.testtoolkit.log
import java.lang.reflect.Modifier
import kotlin.test.Test

/**
 * # 类型系统测试
 *
 * 测试类型相关功能的正确性
 */
class TypingTest {

  @Test
  fun `测试枚举方法存在性`() {
    val fns = PCB47::class.java.declaredMethods
    val static = fns.filter { it.name == "get" || it.name == "findVal" }.firstOrNull { Modifier.isStatic(it.modifiers) }
    log.info("找到的静态方法: {}", static)
  }
}
