package net.yan100.compose.meta.client

import net.yan100.compose.meta.types.TypeKind
import net.yan100.compose.typing.UserAgents
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ClientTypeTest {

  @Test
  fun 能正常以反射方式获取枚举() {
    val t =
      ClientType(
        typeName = UserAgents::class.qualifiedName!!,
        typeKind = TypeKind.ENUM_CLASS,
        enumConstants = UserAgents.entries.map { it.name to it.ordinal }.toMap(),
      )
    val kclass = t.resolveKotlin()
    assertEquals(UserAgents::class, kclass)
    val constants = t.resolveEnumConstants()
    assertNotEquals(0, constants.size)
    println(constants)
  }
}
