package net.yan100.compose.client.contexts

import jakarta.annotation.Resource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import net.yan100.compose.client.interceptors.QualifierNameInterceptor
import net.yan100.compose.client.interceptors.ts.TsEnumInterceptor
import net.yan100.compose.client.interceptors.ts.TsJimmerInterceptor
import net.yan100.compose.client.interceptors.ts.TsTypeAliasInterceptor
import net.yan100.compose.meta.client.ClientApiStubs
import net.yan100.compose.meta.client.ClientType
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class KtToKtContextTest {
  lateinit var stub: ClientApiStubs
    @Resource set

  @Test
  fun `addInterceptor 后置添加拦截器并能获取到`() {
    val ctx =
      KtToKtContext(
        stub,
        QualifierNameInterceptor.KotlinNameToJavaNameInterceptor(),
      )
    assertEquals(1, ctx.getInterceptors().size)
    ctx.addInterceptor(TsTypeAliasInterceptor())
    assertEquals(2, ctx.getInterceptors().size)
    ctx.addInterceptors(listOf(TsJimmerInterceptor(), TsEnumInterceptor()))
    assertEquals(4, ctx.getInterceptors().size)
  }

  @Test
  fun `拦截器上下文，装载名称拦截器后，处理了所有定义的名称`() {
    val ctx =
      KtToKtContext(
        stub,
        QualifierNameInterceptor.KotlinNameToJavaNameInterceptor(),
      )
    val def = ctx.definitions
    val any = def.find { it.typeName == "kotlin.Any" }
    val obj = def.find { it.typeName == "java.lang.Object" }

    assertNotNull(obj)
    assertNull(any)

    fun checkSuperTypeNames(clientType: ClientType) {
      clientType.superTypes.forEach {
        assertNotNull(Class.forName(it.typeName))
        if (it.superTypes.isNotEmpty()) {
          checkSuperTypeNames(it)
        }
      }
    }

    def.forEach { ct ->
      if (ct.isAlias != true) {
        assertNotNull(Class.forName(ct.typeName))
      } else {
        assertNotNull(Class.forName(ct.aliasForTypeName))
      }
      checkSuperTypeNames(ct)
    }
  }
}
