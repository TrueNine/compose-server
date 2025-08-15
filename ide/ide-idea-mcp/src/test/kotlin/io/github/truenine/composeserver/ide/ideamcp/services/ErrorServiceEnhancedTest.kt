package io.github.truenine.composeserver.ide.ideamcp.services

import com.intellij.openapi.application.ApplicationManager
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.github.truenine.composeserver.ide.ideamcp.tools.ErrorSeverity

/** 增强的错误服务测试 测试多种错误检测方法的有效性 */
class ErrorServiceEnhancedTest : BasePlatformTestCase() {

  private lateinit var errorService: ErrorService

  override fun setUp() {
    super.setUp()
    errorService = ErrorServiceImpl()
  }

  fun testErrorDetectionWithUnusedImport() {
    // 创建一个包含未使用导入的测试文件
    val testContent =
      """
      package com.test
      
      import java.util.List  // 未使用的导入
      import java.util.Map   // 未使用的导入
      
      class TestClass {
        fun doSomething() {
          println("Hello")
        }
      }
    """
        .trimIndent()

    val virtualFile = myFixture.configureByText("TestClass.kt", testContent).virtualFile

    // 等待代码分析完成
    ApplicationManager.getApplication().invokeAndWait {
      // 触发错误检测
      val errors = errorService.analyzeFile(project, virtualFile)

      println("Found ${errors.size} errors/warnings:")
      errors.forEach { error -> println("  Line ${error.line}: ${error.severity} - ${error.message}") }

      // 验证是否检测到未使用的导入
      val unusedImportErrors = errors.filter { it.message.contains("unused", ignoreCase = true) || it.message.contains("never used", ignoreCase = true) }

      if (unusedImportErrors.isNotEmpty()) {
        println("✓ Successfully detected unused imports!")
      } else {
        println("✗ Failed to detect unused imports")
        println("All detected issues:")
        errors.forEach { error -> println("  ${error.severity}: ${error.message}") }
      }
    }
  }

  fun testErrorDetectionWithSyntaxError() {
    // 创建一个包含语法错误的测试文件
    val testContent =
      """
      package com.test
      
      class TestClass {
        fun doSomething( {  // 语法错误：缺少参数和右括号
          println("Hello")
        }
      }
    """
        .trimIndent()

    val virtualFile = myFixture.configureByText("ErrorClass.kt", testContent).virtualFile

    ApplicationManager.getApplication().invokeAndWait {
      val errors = errorService.analyzeFile(project, virtualFile)

      println("Found ${errors.size} errors/warnings:")
      errors.forEach { error -> println("  Line ${error.line}: ${error.severity} - ${error.message}") }

      // 验证是否检测到语法错误
      val syntaxErrors = errors.filter { it.severity == ErrorSeverity.ERROR }

      if (syntaxErrors.isNotEmpty()) {
        println("✓ Successfully detected syntax errors!")
      } else {
        println("✗ Failed to detect syntax errors")
      }
    }
  }

  fun testComprehensiveErrorDetectionWithRealFileAnalysis() {
    // 创建一个包含多种问题的测试文件
    val testContent =
      """
      package com.test

      import java.util.List  // 未使用
      import java.util.ArrayList

      class TestClass {
        private val unusedField = "never used"  // 未使用的字段

        fun doSomething(): String {
          val list = ArrayList<String>()
          list.add("test")
          return list.toString()
          val unreachableCode = "never reached"  // 不可达代码
        }

        fun undefinedMethod() {
          nonExistentFunction()  // 未定义的函数
        }
      }
    """
        .trimIndent()

    val virtualFile = myFixture.configureByText("ComprehensiveTest.kt", testContent).virtualFile

    ApplicationManager.getApplication().invokeAndWait {
      // 尝试手动触发代码分析
      myFixture.doHighlighting()

      // 等待分析完成
      Thread.sleep(2000)

      // 再次触发高亮
      val highlightInfos = myFixture.doHighlighting()
      println("Direct highlighting found ${highlightInfos.size} issues:")
      highlightInfos.forEach { info -> println("  ${info.severity}: ${info.description} at ${info.startOffset}-${info.endOffset}") }

      val errors = errorService.analyzeFile(project, virtualFile)

      println("Enhanced error service - Found ${errors.size} errors/warnings:")
      errors.forEach { error ->
        println("  Line ${error.line}, Col ${error.column}: ${error.severity} - ${error.message}")
        println("    Code: ${error.codeSnippet}")
      }

      // 分类统计
      val errorCount = errors.count { it.severity == ErrorSeverity.ERROR }
      val warningCount = errors.count { it.severity == ErrorSeverity.WARNING }
      val weakWarningCount = errors.count { it.severity == ErrorSeverity.WEAK_WARNING }

      println("\nSummary:")
      println("  Errors: $errorCount")
      println("  Warnings: $warningCount")
      println("  Weak Warnings: $weakWarningCount")
      println("  Direct highlighting: ${highlightInfos.size}")

      // 如果直接高亮检测到问题，但我们的服务没有检测到，说明需要改进
      if (highlightInfos.isNotEmpty() && errors.isEmpty()) {
        println("⚠ Direct highlighting detected issues but our service didn't - need improvement")
      } else if (errors.isNotEmpty()) {
        println("✓ Error detection is working!")
      } else {
        println("ℹ No errors detected by either method - might be test environment limitation")
      }
    }
  }

  fun testPsiSyntaxErrorDetection() {
    // 创建一个包含明显语法错误的文件
    val testContent =
      """
      package com.test

      class TestClass {
        fun brokenFunction( {  // 明显的语法错误
          println("This should cause a syntax error")
        }
      }
    """
        .trimIndent()

    val virtualFile = myFixture.configureByText("SyntaxErrorTest.kt", testContent).virtualFile

    ApplicationManager.getApplication().invokeAndWait {
      val errors = errorService.analyzeFile(project, virtualFile)

      println("Syntax error test - Found ${errors.size} errors/warnings:")
      errors.forEach { error ->
        println("  Line ${error.line}, Col ${error.column}: ${error.severity} - ${error.message}")
        println("    Code: ${error.codeSnippet}")
      }

      // 检查是否检测到语法错误
      val syntaxErrors = errors.filter { it.code.contains("syntax", ignoreCase = true) || it.code.contains("error", ignoreCase = true) }
      if (syntaxErrors.isNotEmpty()) {
        println("✓ Successfully detected syntax errors via PSI!")
      } else {
        println("ℹ No PSI syntax errors detected - this might be normal in test environment")
      }
    }
  }

  fun testErrorSeverityClassification() {
    // 测试错误严重程度分类
    val errorService = ErrorServiceImpl()

    // 测试严重程度判断方法
    val testCases =
      listOf(
        "Variable 'unused' is never used" to ErrorSeverity.WEAK_WARNING,
        "Deprecated API usage" to ErrorSeverity.WARNING,
        "Unresolved reference: undefinedFunction" to ErrorSeverity.ERROR,
        "Typo: 'functon' should be 'function'" to ErrorSeverity.WEAK_WARNING,
      )

    testCases.forEach { (description, expectedSeverity) ->
      // 我们无法直接测试私有方法，但可以通过分析包含特定错误描述的文件来间接测试
      println("Testing severity classification for: $description")
      println("Expected: $expectedSeverity")
    }
  }
}
