package io.github.truenine.composeserver.ide.ideamcp.tools

import com.intellij.openapi.project.Project
import io.github.truenine.composeserver.ide.ideamcp.services.LibCodeResult
import io.github.truenine.composeserver.ide.ideamcp.services.LibCodeService
import io.mockk.*
import kotlin.test.Test
import kotlin.test.assertTrue

/** Simplified tests for ViewLibCodeTool. */
class ViewLibCodeToolTest {

  @Test
  fun `should accept valid class name`() {
    // Given
    val viewLibCodeTool = ViewLibCodeTool()
    val mockProject = mockk<Project>(relaxed = true)
    val args = ViewLibCodeArgs(fullyQualifiedName = "com.example.TestClass")

    // When
    val response = viewLibCodeTool.handle(mockProject, args)

    // Then
    val responseText = response.toString()
    // Should not contain argument errors when class name is valid, or should indicate success
    assertTrue(!responseText.contains("INVALID_ARGUMENT") || responseText.contains("success"))
  }

  @Test
  fun `should reject empty class name`() {
    // Given
    val viewLibCodeTool = ViewLibCodeTool()
    val mockProject = mockk<Project>(relaxed = true)
    val args = ViewLibCodeArgs(fullyQualifiedName = "")

    // When
    val response = viewLibCodeTool.handle(mockProject, args)

    // Then
    val responseText = response.toString()
    assertTrue(responseText.contains("INVALID_ARGUMENT"))
    assertTrue(responseText.contains("Fully-qualified class name must not be blank"))
  }

  @Test
  fun `should reject invalid class name format`() {
    // Given
    val viewLibCodeTool = ViewLibCodeTool()
    val mockProject = mockk<Project>(relaxed = true)
    val args = ViewLibCodeArgs(fullyQualifiedName = "123.invalid.class.name")

    // When
    val response = viewLibCodeTool.handle(mockProject, args)

    // Then
    val responseText = response.toString()
    assertTrue(responseText.contains("INVALID_ARGUMENT"))
    assertTrue(responseText.contains("Invalid class name format"))
  }

  @Test
  fun `should accept valid parameters`() {
    // Given
    val viewLibCodeTool = ViewLibCodeTool()
    val mockProject = mockk<Project>(relaxed = true)
    val mockLibCodeService = mockk<LibCodeService>()
    val args = ViewLibCodeArgs(fullyQualifiedName = "com.example.TestClass", memberName = "testMethod")

    val mockResult =
      LibCodeResult(
        sourceCode = "public class TestClass {}",
        isDecompiled = false,
        language = "java",
        metadata =
          io.github.truenine.composeserver.ide.ideamcp.services.LibCodeMetadata(
            libraryName = "example-lib",
            version = "1.0.0",
            sourceType = SourceType.SOURCE_JAR,
            documentation = null,
          ),
      )

    every { mockProject.getService(LibCodeService::class.java) } returns mockLibCodeService
    coEvery { mockLibCodeService.getLibraryCode(any(), any(), any()) } returns mockResult

    // When
    val response = viewLibCodeTool.handle(mockProject, args)

    // Then
    val responseText = response.toString()
    assertTrue(responseText.contains("\"success\":true"))
  }
}
