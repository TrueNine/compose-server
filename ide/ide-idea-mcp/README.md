# 🚀 Compose Server MCP Plugin

## 📖 IDEA 插件开发文档

- **[IntelliJ Platform Plugin SDK](https://plugins.jetbrains.com/docs/intellij/welcome.html)** - 官方插件开发文档
- **[Plugin Development Guidelines](https://plugins.jetbrains.com/docs/intellij/plugin-development-guidelines.html)** - 插件开发指南
- **[IntelliJ Platform UI Guidelines](https://plugins.jetbrains.com/docs/intellij/ui-guidelines.html)** - UI 设计指南
- **[Plugin Testing](https://plugins.jetbrains.com/docs/intellij/testing-plugins.html)** - 插件测试指南
- **[Plugin Publishing](https://plugins.jetbrains.com/docs/intellij/publishing-plugin.html)** - 插件发布指南

## 🛠️ 需要实现的工具列表

### 1. TerminalTool
- **功能**：执行终端命令并返回清洗后的输出
- **参数**：
  - `command`: 要执行的命令
  - `workingDirectory`: 工作目录
  - `timeout`: 超时时间（毫秒）
  - `cleanOutput`: 是否清洗输出

### 2. ViewErrorTool
- **功能**：查看文件或文件夹中的错误信息
- **参数**：
  - `path`: 文件或文件夹路径
  - `includeWarnings`: 是否包含警告
  - `includeWeakWarnings`: 是否包含弱警告

### 3. CleanCodeTool
- **功能**：清理和格式化代码
- **参数**：
  - `path`: 文件路径
  - `formatCode`: 是否格式化代码
  - `optimizeImports`: 是否优化导入
  - `runInspections`: 是否运行检查

### 4. ViewLibCodeTool
- **功能**：查看第三方库源代码
- **参数**：
  - `filePath`: 文件路径
  - `fullyQualifiedName`: 完全限定类名
  - `memberName`: 成员方法名
