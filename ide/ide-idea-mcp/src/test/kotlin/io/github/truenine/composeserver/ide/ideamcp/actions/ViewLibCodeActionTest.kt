package io.github.truenine.composeserver.ide.ideamcp.actions

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.github.truenine.composeserver.ide.ideamcp.services.LibCodeMetadata
import io.github.truenine.composeserver.ide.ideamcp.services.LibCodeResult
import io.github.truenine.composeserver.ide.ideamcp.tools.SourceType

/**
 * ViewLibCodeAction 单元测试
 * 测试库代码查看右键菜单动作的功能
 */
class ViewLibCodeActionTest : BasePlatformTestCase() {

  fun testActionCreation() {
    // 测试动作可以正确创建
    val action = ViewLibCodeAction()
    assertNotNull(action)
    assertEquals("查看库代码", action.templateText)
  }

  fun testLibCodeDialogCreation() {
    // 测试库代码对话框可以正确创建
    val metadata = LibCodeMetadata(
      libraryName = "test-lib",
      version = "1.0",
      sourceType = SourceType.SOURCE_JAR,
      documentation = null
    )

    val result = LibCodeResult(
      sourceCode = "", // 使用空字符串避免创建编辑器
      isDecompiled = false,
      language = "java",
      metadata = metadata
    )

    val dialog = LibCodeDialog(project, result, "TestClass")
    assertNotNull(dialog)
    assertEquals("库代码 - TestClass", dialog.title)
  }

  fun testEditorResourceManagement() {
    // 测试编辑器资源管理
    val metadata = LibCodeMetadata(
      libraryName = "test-lib",
      version = "1.0", 
      sourceType = SourceType.SOURCE_JAR,
      documentation = null
    )

    val result = LibCodeResult(
      sourceCode = "public class TestClass { }",
      isDecompiled = false,
      language = "java",
      metadata = metadata
    )

    // 测试对话框创建和销毁不会泄漏编辑器
    val dialog = LibCodeDialog(project, result, "TestClass")
    assertNotNull(dialog)
    
    // 通过反射访问dispose方法进行清理测试
    try {
      val disposeMethod = dialog.javaClass.superclass.getDeclaredMethod("dispose")
      disposeMethod.isAccessible = true
      disposeMethod.invoke(dialog)
      // 如果没有抛出异常，说明dispose方法正常工作
    } catch (e: Exception) {
      fail("dispose方法调用失败: ${e.message}")
    }
  }
}
