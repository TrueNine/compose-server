package net.yan100.compose.client.contexts

import jakarta.annotation.Resource
import net.yan100.compose.client.interceptors.TypeInterceptor
import net.yan100.compose.meta.client.ClientApi
import net.yan100.compose.meta.client.ClientType
import net.yan100.compose.meta.types.TypeKind
import net.yan100.compose.testtookit.log
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

@SpringBootTest
class KotlinGeneratorLauncherTest {
  lateinit var api: ClientApi @Resource set
  

  @Test
  fun 客户端拦截器拦截并转换为了目标类型() {
    val ctx = KotlinGeneratorLauncher(api)
    val typealiasSize = ctx.definitions.filter { it.typeKind == TypeKind.TYPEALIAS }.size
    var processCount = 0
    val interceptor = object : TypeInterceptor() {
      override fun supported(source: ClientType): Boolean {
        return source.typeKind == TypeKind.TYPEALIAS
      }

      override fun process(source: ClientType): ClientType {
        log.info("source: {}", source)
        processCount += 1
        return source.copy(typeName = "newName")
      }
    }
    val result = ctx.handleClientTypeInterceptors(api.definitions, listOf(interceptor))
    assertEquals(typealiasSize, processCount)

    result.filter { it.typeKind == TypeKind.TYPEALIAS }
      .forEach {
        assertEquals("newName", it.typeName)
      }
  }

  @Test
  fun 确保上下文可以修改内置定义并被后置变量采纳() {
    val ctx = KotlinGeneratorLauncher(api)
    val beforeBSize = ctx.builtinDefinitions.size
    ctx.definitions = ctx.definitions.map {
      it.copy(builtin = true)
    }.toMutableList()
    val afterBSize = ctx.builtinDefinitions.size
    assertNotEquals(beforeBSize, afterBSize)
  }

  @Test
  fun 上下文初始化后不会影响后置计算变量() {
    val ctx = KotlinGeneratorLauncher(api, listOf())
    assertEquals(api, ctx.api, "初始化后上下文必须等于原始对象")
    assertFalse("上下文使用的对象不能引用原始对象") { api === ctx.api }
  }
}
