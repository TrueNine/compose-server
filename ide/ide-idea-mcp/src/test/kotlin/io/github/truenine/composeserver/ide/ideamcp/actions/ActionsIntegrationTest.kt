package io.github.truenine.composeserver.ide.ideamcp.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/** 动作集成测试 测试所有右键菜单动作的集成和注册情况 */
class ActionsIntegrationTest : BasePlatformTestCase() {

  fun testAllActionsCanBeCreated() {
    // 测试所有动作都可以正确创建
    assertNotNull(CleanCodeAction())
    assertNotNull(ViewErrorAction())
    assertNotNull(ViewLibCodeAction())
  }

  fun testAllActionsExtendAnAction() {
    // 测试所有动作都继承自 AnAction
    assertTrue(CleanCodeAction() is AnAction)
    assertTrue(ViewErrorAction() is AnAction)
    assertTrue(ViewLibCodeAction() is AnAction)
  }

  fun testActionTextsAreConsistent() {
    // 测试动作文本一致性
    val cleanCodeAction = CleanCodeAction()
    val viewErrorAction = ViewErrorAction()
    val viewLibCodeAction = ViewLibCodeAction()

    assertEquals("清理代码", cleanCodeAction.templateText)
    assertEquals("查看错误", viewErrorAction.templateText)
    assertEquals("查看库代码", viewLibCodeAction.templateText)
  }
}
