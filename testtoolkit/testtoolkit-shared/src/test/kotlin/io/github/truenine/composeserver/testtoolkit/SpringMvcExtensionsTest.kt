package io.github.truenine.composeserver.testtoolkit

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import org.springframework.mock.web.MockMultipartFile

/**
 * # Spring MVC 函数测试
 *
 * 测试 SpringMvcExtensions.kt 中的扩展函数
 *
 * @author TrueNine
 * @since 2025-07-12
 */
class SpringMvcExtensionsTest {

  @Test
  fun `测试 MockMultipartFile copy 函数的默认行为`() {
    log.info("开始测试 MockMultipartFile copy 函数的默认行为")

    val originalContent = "Hello, World!".toByteArray()
    val original = MockMultipartFile("testFile", "test.txt", "text/plain", originalContent)

    val copied = original.copy()

    assertEquals(original.name, copied.name, "复制后的文件名应该相同")
    assertEquals(original.originalFilename, copied.originalFilename, "复制后的原始文件名应该相同")
    assertEquals(original.contentType, copied.contentType, "复制后的内容类型应该相同")
    assertContentEquals(original.bytes, copied.bytes, "复制后的内容应该相同")

    log.info("MockMultipartFile copy 函数默认行为测试完成")
  }

  @Test
  fun `测试 MockMultipartFile copy 函数的自定义参数`() {
    log.info("开始测试 MockMultipartFile copy 函数的自定义参数")

    val originalContent = "Original content".toByteArray()
    val original = MockMultipartFile("originalFile", "original.txt", "text/plain", originalContent)

    val newContent = "New content".toByteArray()
    val copied = original.copy(name = "newFile", originalFilename = "new.txt", contentType = "text/html", content = newContent)

    assertEquals("newFile", copied.name, "复制后的文件名应该是新的名称")
    assertEquals("new.txt", copied.originalFilename, "复制后的原始文件名应该是新的名称")
    assertEquals("text/html", copied.contentType, "复制后的内容类型应该是新的类型")
    assertContentEquals(newContent, copied.bytes, "复制后的内容应该是新的内容")

    log.info("MockMultipartFile copy 函数自定义参数测试完成")
  }

  @Test
  fun `测试 MockMultipartFile copy 函数的部分参数覆盖`() {
    log.info("开始测试 MockMultipartFile copy 函数的部分参数覆盖")

    val originalContent = "Test content".toByteArray()
    val original = MockMultipartFile("testFile", "test.txt", "text/plain", originalContent)

    // 只修改名称，其他保持默认
    val copied1 = original.copy(name = "newName")
    assertEquals("newName", copied1.name, "只修改名称时，名称应该更新")
    assertEquals(original.originalFilename, copied1.originalFilename, "其他属性应该保持不变")
    assertEquals(original.contentType, copied1.contentType, "其他属性应该保持不变")
    assertContentEquals(original.bytes, copied1.bytes, "其他属性应该保持不变")

    // 只修改内容类型
    val copied2 = original.copy(contentType = "application/json")
    assertEquals(original.name, copied2.name, "其他属性应该保持不变")
    assertEquals(original.originalFilename, copied2.originalFilename, "其他属性应该保持不变")
    assertEquals("application/json", copied2.contentType, "只修改内容类型时，内容类型应该更新")
    assertContentEquals(original.bytes, copied2.bytes, "其他属性应该保持不变")

    log.info("MockMultipartFile copy 函数部分参数覆盖测试完成")
  }

  @Test
  fun `测试 MockMultipartFile copy 函数处理空内容`() {
    log.info("开始测试 MockMultipartFile copy 函数处理空内容")

    val original = MockMultipartFile("emptyFile", "empty.txt", "text/plain", ByteArray(0))

    val copied = original.copy()

    assertEquals(original.name, copied.name, "空文件复制后名称应该相同")
    assertEquals(original.originalFilename, copied.originalFilename, "空文件复制后原始文件名应该相同")
    assertEquals(original.contentType, copied.contentType, "空文件复制后内容类型应该相同")
    assertEquals(0, copied.size, "空文件复制后大小应该为0")
    assertContentEquals(ByteArray(0), copied.bytes, "空文件复制后内容应该为空")

    log.info("MockMultipartFile copy 函数处理空内容测试完成")
  }

  @Test
  fun `测试 MockMultipartFile copy 函数处理 null 内容类型`() {
    log.info("开始测试 MockMultipartFile copy 函数处理 null 内容类型")

    val content = "Test content".toByteArray()
    val original =
      MockMultipartFile(
        "testFile",
        "test.txt",
        null, // null content type
        content,
      )

    val copied = original.copy()

    assertEquals(original.name, copied.name, "null内容类型文件复制后名称应该相同")
    assertEquals(original.originalFilename, copied.originalFilename, "null内容类型文件复制后原始文件名应该相同")
    assertEquals(null, copied.contentType, "null内容类型文件复制后内容类型应该仍为null")
    assertContentEquals(original.bytes, copied.bytes, "null内容类型文件复制后内容应该相同")

    log.info("MockMultipartFile copy 函数处理 null 内容类型测试完成")
  }
}
