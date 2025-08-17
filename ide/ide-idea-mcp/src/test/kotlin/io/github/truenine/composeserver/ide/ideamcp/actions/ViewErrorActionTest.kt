package io.github.truenine.composeserver.ide.ideamcp.actions

import com.intellij.testFramework.fixtures.BasePlatformTestCase

/** ViewErrorAction 单元测试 测试错误查看右键菜单动作的功能 */
class ViewErrorActionTest : BasePlatformTestCase() {

  fun testActionCreation() {
    // 测试动作可以正确创建
    val action = ViewErrorAction()
    assertNotNull(action)
    assertEquals("查看错误", action.templateText)
  }

  fun testErrorViewOptionsDialogCreation() {
    // 测试错误查看选项对话框可以正确创建
    val dialog = ErrorViewOptionsDialog(project)
    assertNotNull(dialog)
    assertEquals("错误查看选项", dialog.title)
  }
}
