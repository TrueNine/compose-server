package net.yan100.compose.client.domain

import net.yan100.compose.client.domain.entries.TsFile
import net.yan100.compose.client.domain.entries.TsName
import net.yan100.compose.meta.client.ClientType
import net.yan100.compose.testtookit.assertNotBlank
import net.yan100.compose.testtookit.log
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.Writer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TsFileTest {
  @Test
  fun `SingleInterface 正确地序列化了接口类型`() {
    val result = TsFile.SingleInterface(interfaces)

    assertNotBlank(result.code)
    assertEquals(4, result.imports.size, "引入了父类，包含了该父类 $result")
    assertTrue {
      result.imports.any {
        it.fromPath.contains("net_yan100_compose")
      }
    }

    assertTrue { result.code.contains("import") }
    assertTrue { result.code.contains("extends ") }

    newWriterFile(result.fileName.toString() + ".ts") {
      it.write(result.code)
      log.info(result.code)
    }
  }


  private fun newWriterFile(name: String, block: ((Writer) -> Unit)? = null): File {
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


  private val interfaces = TsScope.Interface(
    generics = listOf(
      TsGeneric.Defined(TsName.Name("<net.yan100.compose.AInterface::[] T1>"), 0),
      TsGeneric.Defined(TsName.Name("<net.yan100.compose.AInterface::[out] T2>"), 1),
      TsGeneric.Defined(TsName.Name("<net.yan100.compose.AInterface::[out] T3>"), 2),
      TsGeneric.Defined(TsName.Name("T4"), 3),
    ),
    properties = listOf(
      TsTypeProperty(
        name = TsName.Name("ab"),
        partial = true,
        defined = TsTypeVal.String
      ),
      TsTypeProperty(
        name = TsName.Name("ab"),
        defined = TsTypeVal.TypeDef(
          typeName = TsName.Name("File"),
          usedGenerics = listOf(
            TsGeneric.Defined(TsName.Generic("T1"), index = 0),
            TsGeneric.Defined(TsName.Generic("T2"), index = 1)
          )
        )
      ),
      TsTypeProperty(
        name = TsName.Name("c"),
        defined = TsTypeVal.TypeDef(
          typeName = TsName.PathName("Abc", "static/net_yan100_compose")
        )
      ),
      TsTypeProperty(
        name = TsName.Name("obj"),
        defined = TsTypeVal.Object(
          elements = listOf(
            TsTypeProperty(
              name = TsName.Name("a"),
              defined = TsTypeVal.TypeDef(
                typeName = TsName.Name("A")
              )
            )
          )
        )
      ),
      TsTypeProperty(
        name = TsName.Name("acc"),
        defined = TsTypeVal.Any
      )
    ),
    name = TsName.PathName("AInterface", path = "static/net_yan100_compose"),
    meta = ClientType("net.yan100.compose.AInterface"),
    superTypes = listOf(
      TsTypeVal.TypeDef(
        typeName = TsName.PathName("SuperInterface1", "static/net_yan100_compose_server"),
        usedGenerics = listOf(
          TsGeneric.Used(
            used = TsTypeVal.TypeDef(
              typeName = TsName.PathName("Abs", "static/net_yan100_compose")
            ),
            index = 0
          )
        )
      ),
      TsTypeVal.TypeDef(
        typeName = TsName.PathName("SuperInterface2", "static/net_yan100_compose")
      )
    )
  )
}
