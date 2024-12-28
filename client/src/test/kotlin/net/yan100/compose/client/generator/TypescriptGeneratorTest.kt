package net.yan100.compose.client.generator

import jakarta.annotation.Resource
import net.yan100.compose.client.domain.TsScope
import net.yan100.compose.client.domain.entries.TsFile
import net.yan100.compose.client.domain.entries.TsName
import net.yan100.compose.client.interceptors.ts.TsBuiltinInterceptor
import net.yan100.compose.client.interceptors.ts.TsStaticInterceptor
import net.yan100.compose.meta.client.ClientApiStubs
import net.yan100.compose.meta.client.ClientType
import net.yan100.compose.meta.types.TypeKind
import net.yan100.compose.testtookit.assertNotEmpty
import net.yan100.compose.testtookit.log
import org.springframework.boot.test.context.SpringBootTest
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.Writer
import kotlin.test.*

@SpringBootTest
class TypescriptGeneratorTest {
  lateinit var api: ClientApiStubs @Resource set
  lateinit var gen: TypescriptGenerator @Resource set

  @Test
  fun `renderStaticInterfacesToFiles 测试将所有静态接口到文件`() {
    val interfaceFiles = gen.renderStaticInterfacesToFiles()
    interfaceFiles.forEach {
      newWriterFile(
        it.fileName.toString() + "." + it.fileExt,
      ) { writer ->
        writer.write(it.code)
      }
    }
  }

  @Test
  fun `测试 生成文件当当前文件夹`() {
    newWriterFile("Executor.ts") {
      it.write(gen.renderExecutorToFile().code)
    }
    gen.renderEnumsToFiles().forEach {
      newWriterFile(
        it.fileName.toString() + "." + it.fileExt,
      ) { writer ->
        writer.write(it.code)
      }
    }
  }


  @Test
  fun `convertedTsScopes 正确地处理了所有静态接口`() {
    val interfaces = gen.convertedTsScopes.filterIsInstance<TsScope.Interface>()
    assertNotEmpty { interfaces }
    interfaces.forEach { interfaced ->
      interfaced.name is TsName.PathName
    }
  }

  @Test
  fun `convertedTsScopes 正确地处理了 kotlin 枚举类型`() {
    val enums = gen.convertedTsScopes.filterIsInstance<TsScope.Enum>()
    assertNotEmpty { enums }
    enums.forEach { e ->
      e.name is TsName.PathName
    }
  }


  @Test
  fun `interceptorChain，初始化后，上下文内部的拦截器链不能为空，且具备基本的拦截器`() {
    assertNotEquals(0, gen.context.getInterceptors().size)
    val builtin = gen.context.getInterceptors().find { it is TsBuiltinInterceptor }
    val static = gen.context.getInterceptors().find { it is TsStaticInterceptor }
    assertNotNull(builtin)
    assertNotNull(static)
  }


  @Test
  fun `convertedTsScopes 后置初始化后，将所有 kotlin 类型映射到 ts 类型`() {
    val definitions = gen.context.definitions
    val cs = gen.convertedTsScopes
    assertNotEquals(0, definitions.size)
    assertEquals(definitions.size, cs.size, "所有目标类型转换数量一致")
    assertNotEquals(
      0,
      definitions.count { it.typeKind == TypeKind.TYPEALIAS },
      "存根内应当拥有至少一个别名定义"
    )
    assertEquals(
      definitions.count { it.typeKind == TypeKind.TYPEALIAS },
      cs.count { it is TsScope.TypeAlias },
      "应当转换足额的类型别名数量"
    )
  }


  @Test
  fun `renderEnumsToFiles 生成全部的 kotlin 类型到 enum_ts 文件`() {
    val allEnumKtClientTypes = api.definitions.filter { it.typeKind == TypeKind.ENUM_CLASS }
    assertNotEquals(0, allEnumKtClientTypes.size)
    val enums = gen.renderEnumsToFiles()
    assertEquals(allEnumKtClientTypes.size, enums.size)
    enums.forEach { e ->
      log.info("enum file: {}", e)
      assertIs<TsFile.SingleEnum>(e)
      assertNotNull(e.code)
      assertNotEquals("", e.code)
    }
  }

  @Test
  fun `renderEnum 生成正确的 ts 枚举`() {
    val tsFile = gen.renderEnum(
      TsScope.Enum(
        meta = ClientType.none(),
        name = TsName.Name("ISO4217"),
        constants = mapOf(
          "CNY" to "CNY",
          "HKD" to "HKD"
        )
      )
    )
    assertIs<TsFile.SingleEnum>(tsFile)
    assertEquals(1, tsFile.exports.size)
    assertEquals(1, tsFile.usedNames.size)
    assertEquals(
      """
      export enum ISO4217 {
        CNY = 'CNY',
        HKD = 'HKD'
      }
    """.trimIndent().plus("\n"), tsFile.code
    )
  }


  @Test
  fun `renderExecutorToFile 生成正确的 Executor ts 文件`() {
    val tsFile = gen.renderExecutorToFile()
    assertNotNull(tsFile)
    assertNotNull(tsFile.code)
    assertIs<TsFile.SingleTypeUtils>(tsFile)
    val body = tsFile.code
    assertTrue {
      body.isNotBlank()
    }
    log.info("body: {}", body)
    val expectResult = """
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
    assertEquals(expectResult, body)
    assertEquals(1, tsFile.exports.size)
    assertEquals(TsName.Name("Executor"), tsFile.fileName)
  }


  fun newWriterFile(name: String, block: ((Writer) -> Unit)? = null): File {
    val dir = File(this::class.java.getResource("/")?.file ?: error("文件不存在"))
    log.info("dir: {}", dir)
    assertTrue { dir.exists() }
    val a = dir.resolve(name)
    log.info("create file: {}", a)
    if (a.exists()) {
      a.delete()
    }
    if (a.parentFile?.exists() == false) {
      a.parentFile.mkdirs()
    }
    a.createNewFile()
    block?.let { b ->
      BufferedWriter(FileWriter(a, false)).use {
        b(it)
      }
    }
    return a
  }
}
