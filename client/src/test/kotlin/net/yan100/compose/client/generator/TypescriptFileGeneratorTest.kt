package net.yan100.compose.client.generator

import jakarta.annotation.Resource
import net.yan100.compose.client.domain.TypescriptEnum
import net.yan100.compose.client.domain.TypescriptFile
import net.yan100.compose.client.domain.TypescriptFileType
import net.yan100.compose.meta.client.ClientApi
import net.yan100.compose.meta.types.TypeKind
import net.yan100.compose.testtookit.assertNotEmpty
import net.yan100.compose.testtookit.log
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.*

@SpringBootTest
class TypescriptFileGeneratorTest {
  lateinit var api: ClientApi @Resource set
  lateinit var gen: TypescriptFileGenerator @Resource set

  @Test
  fun `renderEnumsToFiles 能够生成全部的 kotlin 类型到 enum_ts 文件`() {
    val allEnumKtClientTypes = api.definitions.filter { it.typeKind == TypeKind.ENUM_CLASS }
    assertNotEquals(0, allEnumKtClientTypes.size)
    val enums = gen.renderEnumsToFiles(allEnumKtClientTypes)
    assertEquals(allEnumKtClientTypes.size, enums.size)
    enums.forEach { e ->
      log.info("enum file: {}", e)
      assertIs<TypescriptFile.Enum>(e)
      assertEquals(TypescriptFileType.DOMAIN_ENUM_CLASS, e.fileType)
      assertNotNull(e.code)
      assertNotEquals("", e.code)
    }
  }

  @Test
  fun `renderEnum 生成正确的枚举`() {
    val tsFile = gen.renderEnum(
      TypescriptEnum(
        name = "ISO4217",
        isExport = true,
        isString = true,
        constants = mapOf(
          "CNY" to "CNY",
          "HKD" to "HKD"
        )
      )
    )
    assertEquals(TypescriptFileType.DOMAIN_ENUM_CLASS, tsFile.fileType)
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
    assertEquals(TypescriptFileType.STATIC_UTILS, tsFile.fileType)
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

  @Test
  fun 每个服务应该至少有一个操作端点() {
    val stub = gen.mappedStubs
    assertNotEquals(0, stub.services.size)
    stub.services.forEach { service ->
      service.operations.forEach { operation ->
        val reqInfo = operation.requestInfo
        assertNotNull(reqInfo)
        assertNotEmpty { reqInfo.mappedUris }
        reqInfo.mappedUris.forEach { assertTrue { it.isNotBlank() } }
        assertNotEquals(0, reqInfo.supportedMethods.size)
        log.info("requestInfo: {}", operation.requestInfo)
      }
    }
  }
}
