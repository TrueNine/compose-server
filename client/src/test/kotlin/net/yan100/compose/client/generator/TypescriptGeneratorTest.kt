package net.yan100.compose.client.generator

import jakarta.annotation.Resource
import net.yan100.compose.client.domain.TypescriptScope
import net.yan100.compose.client.domain.entries.TypescriptFile
import net.yan100.compose.client.domain.entries.TypescriptName
import net.yan100.compose.meta.client.ClientApiStubs
import net.yan100.compose.meta.types.TypeKind
import net.yan100.compose.testtookit.log
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.*

@SpringBootTest
class TypescriptGeneratorTest {
  lateinit var api: ClientApiStubs @Resource set
  lateinit var gen: TypescriptGenerator @Resource set

  @Test
  fun `renderEnumsToFiles 能够生成全部的 kotlin 类型到 enum_ts 文件`() {
    val allEnumKtClientTypes = api.definitions.filter { it.typeKind == TypeKind.ENUM_CLASS }
    assertNotEquals(0, allEnumKtClientTypes.size)
    val enums = gen.renderEnumsToFiles(allEnumKtClientTypes)
    assertEquals(allEnumKtClientTypes.size, enums.size)
    enums.forEach { e ->
      log.info("enum file: {}", e)
      assertIs<TypescriptFile.SingleEnum>(e)
      assertNotNull(e.code)
      assertNotEquals("", e.code)
    }
  }

  @Test
  fun `renderEnum 生成正确的枚举`() {
    val tsFile = gen.renderEnum(
      TypescriptScope.Enum(
        name = TypescriptName.Name("ISO4217"),
        constants = mapOf(
          "CNY" to "CNY",
          "HKD" to "HKD"
        )
      )
    )
    assertIs<TypescriptFile.SingleEnum>(tsFile)
    assertEquals(1, tsFile.exports.size)
    assertEquals(1, tsFile.usedNames.size)
    assertEquals(
      """
      export enum ISO4217 {
        CNY = "CNY",
        HKD = "HKD"
      }
    """.trimIndent().plus("\n"), tsFile.code
    )
  }


  @Test
  fun `renderExecutor 生成正确的 Executor`() {
    val tsFile = gen.renderExecutor()
    assertNotNull(tsFile)
    assertNotNull(tsFile.code)
    assertIs<TypescriptFile.SingleTypeUtils>(tsFile)
    val body = tsFile.code
    assertTrue {
      body.isNotBlank()
    }
    log.info("body: {}", body)
    val e = """
type HTTPMethod = 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH' | 'HEAD' | 'OPTIONS' | 'TRACE'
type BodyType = 'json' | 'form'
export type Executor = (requestOptions: {
  readonly uri: `/${'$'}{string}`
  readonly method: HTTPMethod
  readonly headers?: {readonly [key: string]: string}
  readonly body?: unknown
  readonly bodyType?: BodyType
}) => Promise<unknown>
""".trimIndent().plus("\n")
    assertEquals(e, body)
  }


}
