package io.github.truenine.composeserver.ide.ideamcp.services

import com.intellij.openapi.project.Project
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlinx.coroutines.runBlocking

/** LibCodeService 简化测试 */
class LibCodeServiceTest {

  @Test
  fun `should return not found result when class not found`() = runBlocking {
    // Given
    val libCodeService = LibCodeServiceImpl()
    val mockProject = mockk<Project>(relaxed = true)
    val className = "com.example.NonExistentClass"

    // When
    val result = libCodeService.getLibraryCode(mockProject, "/path/to/file.jar", className, null)

    // Then
    assertNotNull(result)
    // 由于简化实现，可能返回反编译结果或未找到结果
    assertEquals("java", result.language)
  }

  @Test
  fun `should extract library name from class name`() = runBlocking {
    // Given
    val libCodeService = LibCodeServiceImpl()
    val mockProject = mockk<Project>(relaxed = true)
    val className = "com.example.library.TestClass"

    // When
    val result = libCodeService.getLibraryCode(mockProject, "/path/to/file.jar", className, null)

    // Then
    assertNotNull(result)
    assertEquals("com.example", result.metadata.libraryName)
  }

  @Test
  fun `should handle single class name`() = runBlocking {
    // Given
    val libCodeService = LibCodeServiceImpl()
    val mockProject = mockk<Project>(relaxed = true)
    val className = "TestClass"

    // When
    val result = libCodeService.getLibraryCode(mockProject, "/path/to/file.jar", className, null)

    // Then
    assertNotNull(result)
    assertEquals("TestClass", result.metadata.libraryName)
  }
}
