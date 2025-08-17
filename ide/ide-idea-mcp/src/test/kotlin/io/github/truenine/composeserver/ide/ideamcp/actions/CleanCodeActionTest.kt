package io.github.truenine.composeserver.ide.ideamcp.actions

import com.intellij.testFramework.fixtures.BasePlatformTestCase

/** CleanCodeAction 单元测试 测试代码清理右键菜单动作的功能 */
class CleanCodeActionTest : BasePlatformTestCase() {

  fun testActionCreation() {
    // 测试动作可以正确创建
    val action = CleanCodeAction()
    assertNotNull(action)
    assertEquals("清理代码", action.templateText)
  }

  fun testCleanOptionsDialogCreation() {
    // 测试清理选项对话框可以正确创建
    val dialog = CleanOptionsDialog(project)
    assertNotNull(dialog)
    assertEquals("代码清理选项", dialog.title)
  }
}
