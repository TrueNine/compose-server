# 错误捕获过滤器 (ErrorCaptureFilter)

## 概述

错误捕获过滤器是基于 JetBrains IntelliJ Platform 的 `HighlightErrorFilter` 接口实现的语法错误捕获系统。它可以拦截所有的语法错误，并提供灵活的过滤和处理机制。

## 功能特性

### 1. 全面的错误捕获
- 捕获所有 `PsiErrorElement` 语法错误
- 记录错误的详细信息（位置、描述、代码片段等）
- 支持按文件路径分组存储错误信息

### 2. 智能错误过滤
- 支持在特定上下文中抑制错误显示
- 内置过滤规则：
  - 测试文件中的不完整代码错误
  - 注释中的代码片段错误
  - Markdown 文件中的代码块错误（可扩展）

### 3. 实时错误监控
- 实时捕获新产生的语法错误
- 支持错误统计和分析
- 提供错误清理和管理功能

## 架构设计

```
ErrorCaptureFilterManager (单例管理器)
    ↓
ErrorCaptureFilter (实现 HighlightErrorFilter)
    ↓
CapturedErrorInfo (错误信息数据类)
```

### 核心组件

1. **ErrorCaptureFilter**: 主要的过滤器实现
2. **ErrorCaptureFilterManager**: 单例管理器，确保全局唯一实例
3. **CapturedErrorInfo**: 捕获的错误信息数据结构
4. **ErrorService**: 集成错误捕获功能的服务接口

## 使用方法

### 1. 基本使用

```kotlin
// 获取过滤器实例
val errorFilter = ErrorCaptureFilterManager.getInstance()

// 获取指定文件的捕获错误
val capturedErrors = errorFilter.getCapturedErrors(filePath)

// 转换为 ErrorInfo 格式
val errorInfos = capturedErrors.map { capturedError ->
    ErrorInfo(
        line = capturedError.line,
        column = capturedError.column,
        severity = ErrorSeverity.ERROR,
        message = capturedError.errorDescription,
        code = "syntax-error",
        codeSnippet = capturedError.elementText,
        quickFixes = emptyList()
    )
}
```

### 2. 通过 ErrorService 使用

```kotlin
// 注入 ErrorService
val errorService = project.getService(ErrorService::class.java)

// 获取包含语法错误的所有问题
val allProblems = errorService.getProblemsForFile(project, virtualFile)

// 仅获取语法错误
val syntaxErrors = errorService.getCapturedSyntaxErrors(project, virtualFile)
```

### 3. 错误管理

```kotlin
val errorFilter = ErrorCaptureFilterManager.getInstance()

// 获取所有捕获的错误
val allErrors = errorFilter.getAllCapturedErrors()

// 清除指定文件的错误
errorFilter.clearCapturedErrors(filePath)

// 清除所有错误
errorFilter.clearAllCapturedErrors()
```

## 配置和扩展

### 1. 插件注册

在 `plugin.xml` 中注册过滤器：

```xml
<extensions defaultExtensionNs="com.intellij">
    <highlightErrorFilter
        implementation="io.github.truenine.composeserver.ide.ideamcp.services.ErrorCaptureFilter"/>
</extensions>
```

### 2. 自定义过滤规则

可以通过修改 `shouldShowError` 方法来添加自定义过滤规则：

```kotlin
private fun shouldShowError(element: PsiErrorElement): Boolean {
    val errorDescription = element.errorDescription
    val containingFile = element.containingFile
    
    // 自定义过滤逻辑
    when {
        // 在特定文件类型中忽略某些错误
        containingFile?.name?.endsWith(".template") == true -> return false
        
        // 忽略特定的错误类型
        errorDescription.contains("experimental", ignoreCase = true) -> return false
        
        // 其他自定义规则...
        else -> return true
    }
}
```

## API 参考

### ErrorCaptureFilter

#### 主要方法

- `shouldHighlightErrorElement(element: PsiErrorElement): Boolean`
  - 决定是否高亮显示错误元素
  - 同时捕获错误信息

- `getCapturedErrors(filePath: String): List<CapturedErrorInfo>`
  - 获取指定文件的捕获错误

- `getAllCapturedErrors(): Map<String, List<CapturedErrorInfo>>`
  - 获取所有捕获的错误

- `clearCapturedErrors(filePath: String)`
  - 清除指定文件的错误

- `clearAllCapturedErrors()`
  - 清除所有错误

### CapturedErrorInfo

#### 数据字段

```kotlin
data class CapturedErrorInfo(
    val filePath: String,        // 文件路径
    val line: Int,               // 行号
    val column: Int,             // 列号
    val errorDescription: String, // 错误描述
    val elementText: String,     // 错误元素文本
    val timestamp: Long          // 捕获时间戳
)
```

## 最佳实践

### 1. 性能优化

- 定期清理不需要的错误记录
- 避免在高频操作中频繁访问捕获的错误
- 使用批量操作处理大量错误

### 2. 内存管理

```kotlin
// 定期清理旧的错误记录
val cutoffTime = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1)
errorFilter.getAllCapturedErrors().forEach { (filePath, errors) ->
    val recentErrors = errors.filter { it.timestamp > cutoffTime }
    if (recentErrors.size < errors.size) {
        errorFilter.clearCapturedErrors(filePath)
        // 重新添加最近的错误（如果需要）
    }
}
```

### 3. 错误分析

```kotlin
// 分析错误模式
val errorPatterns = errorFilter.getAllCapturedErrors()
    .values.flatten()
    .groupBy { it.errorDescription }
    .mapValues { it.value.size }
    .toList()
    .sortedByDescending { it.second }

// 识别最常见的错误类型
val topErrors = errorPatterns.take(10)
```

## 故障排除

### 常见问题

1. **过滤器未生效**
   - 检查 `plugin.xml` 中的注册配置
   - 确认插件已正确加载

2. **错误未被捕获**
   - 检查文件是否有效
   - 确认 PSI 树已正确构建

3. **内存使用过高**
   - 定期清理错误记录
   - 检查是否有内存泄漏

### 调试技巧

```kotlin
// 启用详细日志
private val logger = LoggerFactory.getLogger(ErrorCaptureFilter::class.java)

// 在关键位置添加日志
logger.debug("Captured error: {} at {}:{}", 
    errorDescription, line, column)
```

## 示例代码

完整的使用示例请参考：
- `ErrorCaptureExample.kt` - 基本使用示例
- `ErrorCaptureFilterTest.kt` - 单元测试示例

## 相关文档

- [JetBrains 语法错误文档](https://plugins.jetbrains.com/docs/intellij/syntax-errors.html#controlling-syntax-errors-highlighting)
- [HighlightErrorFilter API](https://github.com/JetBrains/intellij-community/tree/idea/252.23892.409/platform/analysis-api/src/com/intellij/codeInsight/highlighting/HighlightErrorFilter.java)
