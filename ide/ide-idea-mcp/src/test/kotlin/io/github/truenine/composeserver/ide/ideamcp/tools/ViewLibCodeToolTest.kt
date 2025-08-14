package io.github.truenine.composeserver.ide.ideamcp.tools

import com.intellij.openapi.project.Project
import io.github.truenine.composeserver.ide.ideamcp.services.LibCodeResult
import io.github.truenine.composeserver.ide.ideamcp.services.LibCodeService
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * ViewLibCodeTool 简化测试
 */
class ViewLibCodeToolTest {

  @Test
  fun `should reject empty file path`() {
    // Given
    val viewLibCodeTool = ViewLibCodeTool()
    val mockProject = mockk<Project>(relaxed = true)
    val args = ViewLibCodeArgs(
      filePath = "",
      fullyQualifiedName = "com.example.TestClass"
    )

    // When
    val response = viewLibCodeTool.handle(mockProject, args)

    // Then
    val responseText = response.toString()
    assertTrue(responseText.contains("INVALID_ARGUMENT"))
    assertTrue(responseText.contains("文件路径不能为空"))
  }

  @Test
  fun `should reject empty class name`() {
    // Given
    val viewLibCodeTool = ViewLibCodeTool()
    val mockProject = mockk<Project>(relaxed = true)
    val args = ViewLibCodeArgs(
      filePath = "/path/to/file.jar",
      fullyQualifiedName = ""
    )

    // When
    val response = viewLibCodeTool.handle(mockProject, args)

    // Then
    val responseText = response.toString()
    assertTrue(responseText.contains("INVALID_ARGUMENT"))
    assertTrue(responseText.contains("完全限定类名不能为空"))
  }

  @Test
  fun `should reject invalid class name format`() {
    // Given
    val viewLibCodeTool = ViewLibCodeTool()
    val mockProject = mockk<Project>(relaxed = true)
    val args = ViewLibCodeArgs(
      filePath = "/path/to/file.jar",
      fullyQualifiedName = "123.invalid.class.name"
    )

    // When
    val response = viewLibCodeTool.handle(mockProject, args)

    // Then
    val responseText = response.toString()
    assertTrue(responseText.contains("INVALID_ARGUMENT"))
    assertTrue(responseText.contains("无效的类名格式"))
  }

  @Test
  fun `should accept valid parameters`() {
    // Given
    val viewLibCodeTool = ViewLibCodeTool()
    val mockProject = mockk<Project>(relaxed = true)
    val mockLibCodeService = mockk<LibCodeService>()
    val args = ViewLibCodeArgs(
      filePath = "/path/to/file.jar",
      fullyQualifiedName = "com.example.TestClass",
      memberName = "testMethod"
    )

    val mockResult = LibCodeResult(
      sourceCode = "public class TestClass {}",
      isDecompiled = false,
      language = "java",
      metadata = io.github.truenine.composeserver.ide.ideamcp.services.LibCodeMetadata(
        libraryName = "example-lib",
        version = "1.0.0",
        sourceType = SourceType.SOURCE_JAR,
        documentation = null
      )
    )

    every { mockProject.getService(LibCodeService::class.java) } returns mockLibCodeService
    coEvery { mockLibCodeService.getLibraryCode(any(), any(), any(), any()) } returns mockResult

    // When
    val response = viewLibCodeTool.handle(mockProject, args)

    // Then
    val responseText = response.toString()
    assertTrue(responseText.contains("\"success\":true"))
  }
}
