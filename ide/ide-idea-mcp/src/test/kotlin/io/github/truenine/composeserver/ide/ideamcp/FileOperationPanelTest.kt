package io.github.truenine.composeserver.ide.ideamcp

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/** FileOperationPanel 单元测试 测试文件操作面板的基本功能 */
class FileOperationPanelTest {

  @Test
  fun `文件操作面板应该正确初始化`() {
    // 由于 FileOperationPanel 依赖 IDEA 项目环境，这里只做基本的类加载测试
    val panelClass = FileOperationPanel::class.java
    assertNotNull(panelClass, "FileOperationPanel 类应该存在")
    assertTrue(panelClass.name.contains("FileOperationPanel"), "类名应该包含 FileOperationPanel")
  }

  @Test
  fun `文件操作面板应该有正确的包名`() {
    val panelClass = FileOperationPanel::class.java
    assertEquals("io.github.truenine.composeserver.ide.ideamcp", panelClass.packageName, "包名应该正确")
  }

  @Test
  fun `文件操作面板应该继承正确的父类`() {
    val panelClass = FileOperationPanel::class.java
    val superClass = panelClass.superclass
    assertNotNull(superClass, "应该有父类")
    assertTrue(superClass.name.contains("SimpleToolWindowPanel"), "应该继承 SimpleToolWindowPanel")
  }

  @Test
  fun `文件操作面板应该有必要的构造函数`() {
    val panelClass = FileOperationPanel::class.java
    val constructors = panelClass.constructors
    assertTrue(constructors.isNotEmpty(), "应该有构造函数")

    val primaryConstructor = constructors.first()
    assertEquals(1, primaryConstructor.parameterCount, "主构造函数应该有一个参数")

    val parameterType = primaryConstructor.parameterTypes.first()
    assertTrue(parameterType.name.contains("Project"), "构造函数参数应该是 Project 类型")
  }
}
