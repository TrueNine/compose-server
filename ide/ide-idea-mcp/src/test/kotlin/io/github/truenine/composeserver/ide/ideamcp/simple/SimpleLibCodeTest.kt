package io.github.truenine.composeserver.ide.ideamcp.simple

import com.intellij.openapi.project.Project
import io.github.truenine.composeserver.ide.ideamcp.services.LibCodeServiceImpl
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking

/** 简单的LibCodeService测试 验证基本功能是否正常 */
class SimpleLibCodeTest {

  @Test
  fun `简单测试 - 验证服务可以正常工作`() = runBlocking {
    // Given
    val libCodeService = LibCodeServiceImpl()
    val mockProject = mockk<Project>(relaxed = true)

    // When
    val result = libCodeService.getLibraryCode(mockProject, "java.lang.String")

    // Then
    assertNotNull(result)
    assertTrue(result.sourceCode.isNotEmpty())
    assertTrue(result.language.isNotEmpty())
    assertNotNull(result.metadata)

    println("✅ 简单测试通过:")
    println("  源码长度: ${result.sourceCode.length}")
    println("  语言: ${result.language}")
    println("  库名: ${result.metadata.libraryName}")
  }
}
