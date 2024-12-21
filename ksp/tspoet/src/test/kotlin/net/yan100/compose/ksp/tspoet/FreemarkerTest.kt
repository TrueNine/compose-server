package net.yan100.compose.ksp.tspoet

import net.yan100.compose.ksp.tspoet.freemarker.FreeMarker
import net.yan100.compose.ksp.tspoet.freemarker.renderString
import net.yan100.compose.meta.client.ClientType
import kotlin.test.Test
import kotlin.test.assertNotNull


class FreemarkerTest {


  @Test
  fun `test render enum`() {
    val writerObjs = listOf(
      ClientType(
        typeName = "net.yan100.ISO",
        enumConstants = mapOf(
          "A" to 1,
          "B" to 2
        )
      ),
      ClientType(
        typeName = "net.yan100.UserAgents",
        enumConstants = mutableMapOf(
          "C" to 1,
          "E" to 2
        )
      )
    ).map {
      it.copy(
        typeName = it.typeName.split(".").last().split("\$").last()
      )
    }
    val t = FreeMarker["META-INF/compose-client/ts/Enum.ts"]?.renderString(mapOf("root" to writerObjs))
    assertNotNull(t)
  }


  @Test
  fun `test free marker launch`() {
    val template = FreeMarker["META-INF/compose-client/ts/Static.ts"]
    val map = mapOf("a" to 1, "b" to 2)
    val result = template?.renderString(map)
    assertNotNull(result)
  }
}
