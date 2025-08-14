package io.github.truenine.composeserver.ide.ideamcp.tools

import io.github.truenine.composeserver.ide.ideamcp.common.ErrorDetails
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json

/** CleanCodeTool 单元测试 */
class CleanCodeToolTest {

  private val cleanCodeTool = CleanCodeTool()

  @Test
  fun `工具名称应该正确`() {
    assertEquals("clean_code", cleanCodeTool.name)
  }

  @Test
  fun `工具描述应该正确`() {
    assertEquals("Clean and format code using IDEA capabilities with comprehensive reporting", cleanCodeTool.description)
  }

  @Test
  fun `CleanCodeArgs 序列化应该正确`() {
    // Given
    val args = CleanCodeArgs(path = "/src/main/kotlin", formatCode = false, optimizeImports = false, runInspections = false)

    // When
    val json = Json.encodeToString(CleanCodeArgs.serializer(), args)
    val decoded = Json.decodeFromString(CleanCodeArgs.serializer(), json)

    // Then
    assertEquals(args.path, decoded.path)
    assertEquals(args.formatCode, decoded.formatCode)
    assertEquals(args.optimizeImports, decoded.optimizeImports)
    assertEquals(args.runInspections, decoded.runInspections)
  }

  @Test
  fun `CleanCodeArgs 默认参数应该正确设置`() {
    // Given
    val args = CleanCodeArgs(path = "/src")

    // Then
    assertEquals("/src", args.path)
    assertTrue(args.formatCode)
    assertTrue(args.optimizeImports)
    assertTrue(args.runInspections)
  }

  @Test
  fun `CleanCodeResult 序列化应该正确`() {
    // Given
    val operation = CleanOperation(type = "FORMAT", description = "代码格式化", filesAffected = 5)

    val result =
      CleanCodeResult(
        success = true,
        path = "/src/main/kotlin",
        processedFiles = 10,
        modifiedFiles = 5,
        operations = listOf(operation),
        summary = "处理了10个文件，修改了5个文件",
        executionTime = 1500L,
        timestamp = 1234567890L,
      )

    // When
    val json = Json.encodeToString(CleanCodeResult.serializer(), result)
    val decoded = Json.decodeFromString(CleanCodeResult.serializer(), json)

    // Then
    assertEquals(result.success, decoded.success)
    assertEquals(result.path, decoded.path)
    assertEquals(result.processedFiles, decoded.processedFiles)
    assertEquals(result.modifiedFiles, decoded.modifiedFiles)
    assertEquals(result.operations.size, decoded.operations.size)
    assertEquals(result.summary, decoded.summary)
    assertEquals(result.executionTime, decoded.executionTime)
    assertEquals(result.timestamp, decoded.timestamp)

    // 验证操作信息
    val decodedOperation = decoded.operations[0]
    assertEquals(operation.type, decodedOperation.type)
    assertEquals(operation.description, decodedOperation.description)
    assertEquals(operation.filesAffected, decodedOperation.filesAffected)
  }

  @Test
  fun `CleanOperation 序列化应该正确`() {
    // Given
    val operation = CleanOperation(type = "OPTIMIZE_IMPORTS", description = "导入优化", filesAffected = 3)

    // When
    val json = Json.encodeToString(CleanOperation.serializer(), operation)
    val decoded = Json.decodeFromString(CleanOperation.serializer(), json)

    // Then
    assertEquals(operation.type, decoded.type)
    assertEquals(operation.description, decoded.description)
    assertEquals(operation.filesAffected, decoded.filesAffected)
  }

  @Test
  fun `CleanCodeErrorResponse 序列化应该正确`() {
    // Given
    val errorResponse =
      CleanCodeErrorResponse(
        success = false,
        error = ErrorDetails(type = "INVALID_ARGUMENT", message = "路径不能为空", suggestions = listOf("检查路径格式", "使用有效的文件路径")),
        path = "",
        timestamp = 1234567890L,
      )

    // When
    val json = Json.encodeToString(CleanCodeErrorResponse.serializer(), errorResponse)
    val decoded = Json.decodeFromString(CleanCodeErrorResponse.serializer(), json)

    // Then
    assertFalse(decoded.success)
    assertEquals(errorResponse.error.type, decoded.error.type)
    assertEquals(errorResponse.error.message, decoded.error.message)
    assertEquals(errorResponse.error.suggestions, decoded.error.suggestions)
    assertEquals(errorResponse.path, decoded.path)
    assertEquals(errorResponse.timestamp, decoded.timestamp)
  }

  @Test
  fun `多个操作的 CleanCodeResult 序列化应该正确`() {
    // Given
    val operations = listOf(CleanOperation("FORMAT", "代码格式化", 8), CleanOperation("OPTIMIZE_IMPORTS", "导入优化", 6), CleanOperation("RUN_INSPECTIONS", "代码检查修复", 3))

    val result =
      CleanCodeResult(
        success = true,
        path = "/src",
        processedFiles = 15,
        modifiedFiles = 12,
        operations = operations,
        summary = "执行了多种清理操作",
        executionTime = 3000L,
        timestamp = System.currentTimeMillis(),
      )

    // When
    val json = Json.encodeToString(CleanCodeResult.serializer(), result)
    val decoded = Json.decodeFromString(CleanCodeResult.serializer(), json)

    // Then
    assertEquals(3, decoded.operations.size)
    assertEquals("FORMAT", decoded.operations[0].type)
    assertEquals("OPTIMIZE_IMPORTS", decoded.operations[1].type)
    assertEquals("RUN_INSPECTIONS", decoded.operations[2].type)
    assertEquals(8, decoded.operations[0].filesAffected)
    assertEquals(6, decoded.operations[1].filesAffected)
    assertEquals(3, decoded.operations[2].filesAffected)
  }

  @Test
  fun `ErrorDetails 序列化应该正确`() {
    // Given
    val errorDetails = ErrorDetails(type = "PERMISSION_DENIED", message = "文件被锁定", suggestions = listOf("关闭其他编辑器", "检查文件权限", "重启IDE"))

    // When
    val json = Json.encodeToString(ErrorDetails.serializer(), errorDetails)
    val decoded = Json.decodeFromString(ErrorDetails.serializer(), json)

    // Then
    assertEquals(errorDetails.type, decoded.type)
    assertEquals(errorDetails.message, decoded.message)
    assertEquals(errorDetails.suggestions.size, decoded.suggestions.size)
    assertEquals("关闭其他编辑器", decoded.suggestions[0])
    assertEquals("检查文件权限", decoded.suggestions[1])
    assertEquals("重启IDE", decoded.suggestions[2])
  }

  @Test
  fun `空操作列表的 CleanCodeResult 序列化应该正确`() {
    // Given
    val result =
      CleanCodeResult(
        success = true,
        path = "/empty",
        processedFiles = 0,
        modifiedFiles = 0,
        operations = emptyList(),
        summary = "没有文件需要处理",
        executionTime = 100L,
        timestamp = System.currentTimeMillis(),
      )

    // When
    val json = Json.encodeToString(CleanCodeResult.serializer(), result)
    val decoded = Json.decodeFromString(CleanCodeResult.serializer(), json)

    // Then
    assertTrue(decoded.success)
    assertEquals(0, decoded.processedFiles)
    assertEquals(0, decoded.modifiedFiles)
    assertTrue(decoded.operations.isEmpty())
    assertEquals("没有文件需要处理", decoded.summary)
  }

  @Test
  fun `部分清理选项的 CleanCodeArgs 序列化应该正确`() {
    // Given
    val args = CleanCodeArgs(path = "/src/test", formatCode = true, optimizeImports = false, runInspections = true)

    // When
    val json = Json.encodeToString(CleanCodeArgs.serializer(), args)
    val decoded = Json.decodeFromString(CleanCodeArgs.serializer(), json)

    // Then
    assertEquals("/src/test", decoded.path)
    assertTrue(decoded.formatCode)
    assertFalse(decoded.optimizeImports)
    assertTrue(decoded.runInspections)
  }
}
