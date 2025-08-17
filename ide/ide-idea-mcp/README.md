# 🚀 Compose Server MCP Plugin

## IDEA 测试指南

- **[Test Overview](https://plugins.jetbrains.com/docs/intellij/testing-plugins.html)** - 测试概述
- **[Test Tests and Fixtures](https://plugins.jetbrains.com/docs/intellij/testing-plugins.html#tests-and-fixtures)** - 测试用例和固定装置
- **[Test Light and Heavy Tests](https://plugins.jetbrains.com/docs/intellij/light-and-heavy-tests.html)** - 轻量级和重量级测试
- **[Test Project and Testdata Directories](https://plugins.jetbrains.com/docs/intellij/test-project-and-testdata-directories.html)** - 项目和测试数据目录
- **[Test Running Tests](https://plugins.jetbrains.com/docs/intellij/writing-tests.html)** - 编写测试
- **[Test Highlighting](https://plugins.jetbrains.com/docs/intellij/testing-highlighting.html)** - 测试高亮
- **[Test FAQ](https://plugins.jetbrains.com/docs/intellij/testing-faq.html)** - 测试常见问题
- **[Integration Test](https://plugins.jetbrains.com/docs/intellij/integration-test.html)** - 集成测试
  + **[Introduction to Integration Tests](https://plugins.jetbrains.com/docs/intellij/integration-tests-intro.html)** - 集成测试介绍
  + **[UI Testing](https://plugins.jetbrains.com/docs/intellij/integration-tests-ui.html)** - UI 测试
  + **[API Testing](https://plugins.jetbrains.com/docs/intellij/integration-tests-api.html)** - API 测试

## 📖 IDEA 插件开发文档

- **[IntelliJ Platform Plugin SDK](https://plugins.jetbrains.com/docs/intellij/welcome.html)** - 官方插件开发文档
- **[Plugin Development Guidelines](https://plugins.jetbrains.com/docs/intellij/plugin-development-guidelines.html)** - 插件开发指南
- **[IntelliJ Platform UI Guidelines](https://plugins.jetbrains.com/docs/intellij/ui-guidelines.html)** - UI 设计指南
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
