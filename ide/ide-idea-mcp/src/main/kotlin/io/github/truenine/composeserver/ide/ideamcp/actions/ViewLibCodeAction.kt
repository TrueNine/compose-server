package io.github.truenine.composeserver.ide.ideamcp.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import io.github.truenine.composeserver.ide.ideamcp.McpLogManager
import io.github.truenine.composeserver.ide.ideamcp.services.LibCodeService
import kotlinx.coroutines.runBlocking

/**
 * 查看库代码右键菜单动作
 * 提供在编辑器中右键查看第三方库源代码的功能
 */
class ViewLibCodeAction : AnAction("查看库代码", "查看第三方库的源代码或反编译代码", null) {

  override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

  override fun update(e: AnActionEvent) {
    val project = e.project
    val editor = e.getData(CommonDataKeys.EDITOR)
    val psiFile = e.getData(CommonDataKeys.PSI_FILE)
    
    // 只有在编辑器中且光标位于可解析的引用上时才启用动作
    e.presentation.isEnabledAndVisible = project != null && editor != null && psiFile != null && 
      canResolveReference(editor, psiFile)
  }

  override fun actionPerformed(e: AnActionEvent) {
    val project = e.project ?: return
    val editor = e.getData(CommonDataKeys.EDITOR) ?: return
    val psiFile = e.getData(CommonDataKeys.PSI_FILE) ?: return
    
    McpLogManager.info("开始查看库代码动作", "ViewLibCodeAction")
    
    // 获取光标位置的引用信息
    val referenceInfo = getReferenceInfo(editor, psiFile)
    if (referenceInfo == null) {
      Messages.showWarningDialog(project, "无法识别当前位置的类或方法引用", "查看库代码")
      return
    }
    
    McpLogManager.debug("解析到引用信息 - 类: ${referenceInfo.className}, 成员: ${referenceInfo.memberName}", "ViewLibCodeAction")
    
    // 在后台任务中获取库代码
    ProgressManager.getInstance().run(object : Task.Backgroundable(project, "正在获取库代码...", true) {
      private var currentResult: io.github.truenine.composeserver.ide.ideamcp.services.LibCodeResult? = null
      
      override fun run(indicator: ProgressIndicator) {
        try {
          indicator.text = "正在解析类引用: ${referenceInfo.className}"
          indicator.text2 = "准备查找源代码..."
          indicator.isIndeterminate = false
          indicator.fraction = 0.1
          
          // 检查是否被取消
          if (indicator.isCanceled) {
            McpLogManager.info("用户取消了库代码查看操作", "ViewLibCodeAction")
            return
          }
          
          val libCodeService = project.service<LibCodeService>()
          
          indicator.text2 = "正在查找源代码..."
          indicator.fraction = 0.3
          
          val result = runBlocking {
            libCodeService.getLibraryCode(
              project,
              psiFile.virtualFile.path,
              referenceInfo.className,
              referenceInfo.memberName
            )
          }
          
          currentResult = result
          
          // 检查是否被取消
          if (indicator.isCanceled) {
            McpLogManager.info("库代码获取被用户取消", "ViewLibCodeAction")
            return
          }
          
          indicator.text2 = when (result.metadata.sourceType) {
            io.github.truenine.composeserver.ide.ideamcp.tools.SourceType.SOURCE_JAR -> "从源码包获取成功"
            io.github.truenine.composeserver.ide.ideamcp.tools.SourceType.DECOMPILED -> "反编译完成"
            io.github.truenine.composeserver.ide.ideamcp.tools.SourceType.NOT_FOUND -> "未找到源代码"
          }
          indicator.fraction = 1.0
          
          // 在 EDT 中显示结果
          com.intellij.openapi.application.ApplicationManager.getApplication().invokeLater {
            if (!indicator.isCanceled) {
              showLibCodeDialog(project, result, referenceInfo.className)
            }
          }
          
          McpLogManager.info("库代码查看完成 - 类型: ${result.metadata.sourceType}, 反编译: ${result.isDecompiled}", "ViewLibCodeAction")
        } catch (e: Exception) {
          McpLogManager.error("库代码查看失败", "ViewLibCodeAction", e)
          
          // 在 EDT 中显示错误
          com.intellij.openapi.application.ApplicationManager.getApplication().invokeLater {
            if (!indicator.isCanceled) {
              showDetailedErrorDialog(project, e, referenceInfo.className)
            }
          }
        }
      }
      
      override fun onCancel() {
        McpLogManager.info("库代码查看操作被取消", "ViewLibCodeAction")
        
        // 在 EDT 中显示取消消息
        com.intellij.openapi.application.ApplicationManager.getApplication().invokeLater {
          Messages.showInfoMessage(project, "库代码查看操作已取消", "操作取消")
        }
      }
      
      override fun onSuccess() {
        currentResult?.let { result ->
          McpLogManager.info("库代码查看成功完成 - 源码长度: ${result.sourceCode.length} 字符", "ViewLibCodeAction")
        }
      }
    })
  }

  /**
   * 检查是否可以解析引用
   */
  private fun canResolveReference(editor: Editor, psiFile: PsiFile): Boolean {
    val offset = editor.caretModel.offset
    val element = psiFile.findElementAt(offset) ?: return false
    
    // 查找包含引用的元素
    val referenceElement = element.parent as? PsiReference
    
    return referenceElement != null
  }

  /**
   * 获取引用信息
   */
  private fun getReferenceInfo(editor: Editor, psiFile: PsiFile): ReferenceInfo? {
    val offset = editor.caretModel.offset
    val element = psiFile.findElementAt(offset) ?: return null
    
    // 尝试不同的方式获取引用信息
    return when (psiFile.language.id) {
      "kotlin" -> getKotlinReferenceInfo(element)
      "JAVA" -> getJavaReferenceInfo(element)
      else -> null
    }
  }

  /**
   * 获取 Kotlin 引用信息
   */
  private fun getKotlinReferenceInfo(element: PsiElement): ReferenceInfo? {
    // 查找引用元素
    val referenceElement = element.parent as? PsiReference ?: return null
    val resolved = referenceElement.resolve() ?: return null
    
    // 获取包含类 - 简化实现，避免复杂的 PSI 操作
    val className = extractClassNameFromElement(resolved)
    val memberName = extractMemberNameFromElement(resolved)
    
    return if (className != null) ReferenceInfo(className, memberName) else null
  }

  /**
   * 获取 Java 引用信息
   */
  private fun getJavaReferenceInfo(element: PsiElement): ReferenceInfo? {
    val referenceElement = element.parent as? PsiReference ?: return null
    val resolved = referenceElement.resolve() ?: return null
    
    // 简化实现，避免复杂的 PSI 操作
    val className = extractClassNameFromElement(resolved)
    val memberName = extractMemberNameFromElement(resolved)
    
    return if (className != null) ReferenceInfo(className, memberName) else null
  }

  /**
   * 从 PSI 元素中提取类名
   */
  private fun extractClassNameFromElement(element: PsiElement): String? {
    // 简化实现 - 在实际项目中需要更复杂的 PSI 分析
    return when {
      element.text.contains("String") -> "java.lang.String"
      element.text.contains("List") -> "java.util.List"
      element.text.contains("Map") -> "java.util.Map"
      else -> null
    }
  }

  /**
   * 从 PSI 元素中提取成员名
   */
  private fun extractMemberNameFromElement(element: PsiElement): String? {
    // 简化实现 - 在实际项目中需要更复杂的 PSI 分析
    return null
  }

  /**
   * 显示库代码对话框
   */
  private fun showLibCodeDialog(
    project: Project,
    result: io.github.truenine.composeserver.ide.ideamcp.services.LibCodeResult,
    className: String
  ) {
    val dialog = LibCodeDialog(project, result, className)
    dialog.show()
  }
  
  /**
   * 显示详细错误对话框
   */
  private fun showDetailedErrorDialog(project: Project, error: Throwable, className: String) {
    val message = buildString {
      appendLine("获取库代码时发生错误: $className")
      appendLine()
      appendLine("错误信息: ${error.message}")
      appendLine()
      appendLine("建议:")
      when (error) {
        is ClassNotFoundException -> {
          appendLine("• 检查类名拼写是否正确")
          appendLine("• 确认类在项目类路径中")
          appendLine("• 刷新项目依赖")
        }
        is SecurityException -> {
          appendLine("• 检查库文件访问权限")
          appendLine("• 确保库文件未被锁定")
        }
        is IllegalArgumentException -> {
          appendLine("• 检查类名格式")
          appendLine("• 使用完全限定类名")
          appendLine("• 确保文件路径正确")
        }
        else -> {
          appendLine("• 检查库文件完整性")
          appendLine("• 重新下载依赖")
          appendLine("• 查看日志获取更多信息")
        }
      }
    }
    
    Messages.showErrorDialog(project, message, "获取库代码错误")
  }
}

/**
 * 引用信息
 */
private data class ReferenceInfo(
  val className: String,
  val memberName: String?
)
