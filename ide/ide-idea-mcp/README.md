# 🚀 Compose Server MCP Plugin

<div align="center">

[![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ%20IDEA-2025.2+-000000?style=for-the-badge&logo=intellij-idea&logoColor=white)](https://www.jetbrains.com/idea/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![MCP Protocol](https://img.shields.io/badge/MCP-1.1.0-FF6B35?style=for-the-badge)](https://modelcontextprotocol.io/)

[![GitHub Stars](https://img.shields.io/github/stars/TrueNine/compose-server?style=for-the-badge&logo=github&logoColor=white)](https://github.com/TrueNine/compose-server/stargazers)
[![License](https://img.shields.io/badge/License-LGPL%202.1-blue?style=for-the-badge&logo=gnu&logoColor=white)](../../LICENSE)

</div>

---

<div align="center">

## 🤖 为 IntelliJ IDEA 提供强大的 MCP 支持和 AI 辅助开发功能

</div>

> **AI 驱动 • 开发效率 • 智能辅助**  
> 集成 Model Context Protocol (MCP) 的 IntelliJ IDEA 插件，提供终端执行、错误查看、代码清理、库代码查看等核心功能

**Compose Server MCP Plugin** 是一个专为 IntelliJ IDEA 设计的插件，它集成了 Model Context Protocol (MCP) 服务器，为开发者提供强大的 AI 辅助功能。通过这个插件，您可以在
IDE 中直接使用 AI 进行终端命令执行、错误诊断、代码清理、库代码查看等操作。

---

## ✨ 核心功能

### 🖥️ **终端命令执行**

- **智能输出清洗** - 自动清理 Gradle、Maven、NPM、NPX 等构建工具的冗余输出
- **AI 友好格式** - 结构化输出格式，便于 AI 理解和处理
- **超时控制** - 支持命令执行超时设置和进程管理
- **工作目录支持** - 支持指定工作目录执行命令

### 🔍 **错误查看工具**

- **全面错误扫描** - 扫描项目中的所有错误、警告和弱警告信息
- **结构化输出** - 包含文件路径、行号、错误类型、错误描述和相关代码行
- **递归文件夹扫描** - 支持对整个项目或指定文件夹进行递归扫描
- **权限检查** - 智能处理文件权限和访问控制

### 🧹 **代码清理工具**

- **多功能清理** - 支持代码格式化、导入优化、代码检查修复等操作
- **批量处理** - 支持对文件或文件夹进行批量代码清理
- **清理报告** - 提供详细的清理结果摘要和统计信息
- **异步执行** - 支持异步执行，不阻塞 IDE 界面

### 📚 **库代码查看**

- **源码提取** - 从 source jar 中提取并显示第三方库源代码
- **字节码反编译** - 当源码不可用时，自动反编译字节码
- **元数据信息** - 提供库名称、版本、文档等元数据信息
- **智能定位** - 支持通过类名和方法名精确定位代码

### 🎛️ **用户界面**

- **调试面板** - 提供日志查看、过滤、搜索、导出等功能
- **终端界面** - 集成终端面板，支持命令历史和输出对比
- **右键菜单** - 在编辑器和项目树中提供便捷的右键操作
- **文件操作面板** - 提供文件选择和批量操作界面

---

## 🛠️ 技术栈

### 🏗️ 核心技术

| 技术                                                                                    | 版本      | 用途                |
|---------------------------------------------------------------------------------------|---------|-------------------|
| **[Kotlin](https://kotlinlang.org/)**                                                 | 2.2.0   | 插件开发语言，现代化 JVM 语言 |
| **[IntelliJ Platform SDK](https://plugins.jetbrains.com/docs/intellij/welcome.html)** | 2025.2+ | IDEA 插件开发框架       |
| **[MCP Protocol](https://modelcontextprotocol.io/)**                                  | 1.1.0   | 模型上下文协议，AI 服务接口标准 |
| **[Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization)**          | 1.7.3   | JSON 序列化和反序列化     |
| **[Kotlin Coroutines](https://github.com/Kotlin/kotlinx.coroutines)**                 | 1.9.0   | 异步编程和并发处理         |

### 🧪 测试框架

| 技术                                                                                          | 版本     | 用途             |
|---------------------------------------------------------------------------------------------|--------|----------------|
| **[JUnit 5](https://junit.org/junit5/)**                                                    | 5.11.4 | 单元测试框架         |
| **[MockK](https://mockk.io/)**                                                              | 1.14.5 | Kotlin Mock 框架 |
| **[IDEA Test Framework](https://plugins.jetbrains.com/docs/intellij/testing-plugins.html)** | -      | IDEA 插件测试框架    |

### 🔗 相关技术栈链接

#### 📖 **IDEA 插件开发文档**

- **[IntelliJ Platform Plugin SDK](https://plugins.jetbrains.com/docs/intellij/welcome.html)** - 官方插件开发文档
- **[Plugin Development Guidelines](https://plugins.jetbrains.com/docs/intellij/plugin-development-guidelines.html)** - 插件开发指南
- **[IntelliJ Platform UI Guidelines](https://plugins.jetbrains.com/docs/intellij/ui-guidelines.html)** - UI 设计指南
- **[Plugin Testing](https://plugins.jetbrains.com/docs/intellij/testing-plugins.html)** - 插件测试指南
- **[Plugin Publishing](https://plugins.jetbrains.com/docs/intellij/publishing-plugin.html)** - 插件发布指南

#### 🤖 **MCP 协议相关**

- **[Model Context Protocol](https://modelcontextprotocol.io/)** - MCP 协议官方文档
- **[MCP Specification](https://spec.modelcontextprotocol.io/)** - MCP 协议规范
- **[Kotlin MCP](https://github.com/modelcontextprotocol/kotlin-sdk)** - Kotlin MCP SDK
- **[MCP Server Examples](https://github.com/modelcontextprotocol/servers)** - MCP 服务器示例
- **[Anthropic MCP](https://github.com/anthropics/mcp)** - Anthropic MCP 实现

#### 🔧 **开发工具和框架**

- **[Kotlin Language](https://kotlinlang.org/)** - Kotlin 编程语言官网
- **[Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)** - 协程开发指南
- **[Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization)** - 序列化库
- **[Gradle Plugin Development](https://docs.gradle.org/current/userguide/custom_plugins.html)** - Gradle 插件开发
- **[JetBrains Runtime](https://github.com/JetBrains/JetBrainsRuntime)** - JetBrains 运行时环境

---

## 🚀 快速开始

### 📋 环境要求

在开始开发或使用插件之前，请确保您的环境满足以下要求：

| 环境                | 最低版本    | 推荐版本    | 说明            |
|-------------------|---------|---------|---------------|
| **IntelliJ IDEA** | 2025.2+ | 2025.2+ | 支持最新的插件开发 API |
| **JDK**           | 24+     | 24+     | 插件运行时环境       |
| **Kotlin**        | 2.2.0+  | 2.2.0+  | 插件开发语言        |
| **Gradle**        | 9.0+    | 9.x     | 构建工具          |

### 📦 安装插件

#### 方式一：从 JetBrains Marketplace 安装（推荐）

1. 打开 IntelliJ IDEA
2. 进入 **Settings** → **Plugins**
3. 搜索 **"Compose Server MCP"**
4. 点击 **Install** 安装插件
5. 重启 IDE

#### 方式二：手动安装

1. 从 [GitHub Releases](https://github.com/TrueNine/compose-server/releases) 下载最新版本的插件 ZIP 文件
2. 打开 IntelliJ IDEA
3. 进入 **Settings** → **Plugins**
4. 点击齿轮图标 → **Install Plugin from Disk...**
5. 选择下载的 ZIP 文件
6. 重启 IDE

### ⚙️ 配置插件

#### MCP 服务器配置

插件安装后，您需要配置 MCP 服务器连接：

1. 打开 **Settings** → **Tools** → **MCP Settings**
2. 配置服务器连接信息：

```json
{
  "mcpServers": {
    "compose-server-mcp": {
      "command": "npx",
      "args": [
        "-y",
        "@truenine/ide-idea-mcp@latest"
      ],
      "env": {
        "LOG_LEVEL": "INFO"
      },
      "disabled": false,
      "autoApprove": [
        "terminal",
        "view_error",
        "clean_code",
        "view_lib_code"
      ]
    }
  }
}
```

#### 调试面板配置

1. 打开 **View** → **Tool Windows** → **MCP Debug**
2. 在调试面板中可以：
  - 查看实时日志
  - 过滤和搜索日志
  - 导出日志文件
  - 清空日志记录

### 🎯 使用指南

#### 1. 终端命令执行

**通过 MCP 工具调用：**

```json
{
  "tool": "terminal",
  "arguments": {
    "command": "gradle build",
    "workingDirectory": "./",
    "timeout": 60000,
    "cleanOutput": true
  }
}
```

**通过终端面板：**

1. 打开 MCP Debug 工具窗口
2. 切换到 Terminal 标签页
3. 输入命令并执行
4. 查看清洗后的输出结果

#### 2. 错误查看

**右键菜单操作：**

1. 在项目树中右键点击文件或文件夹
2. 选择 **MCP Actions** → **View Errors**
3. 查看结构化的错误报告

**MCP 工具调用：**

```json
{
  "tool": "view_error",
  "arguments": {
    "path": "src/main/kotlin",
    "includeWarnings": true,
    "includeWeakWarnings": true
  }
}
```

#### 3. 代码清理

**右键菜单操作：**

1. 在编辑器或项目树中右键点击
2. 选择 **MCP Actions** → **Clean Code**
3. 选择清理选项并执行

**MCP 工具调用：**

```json
{
  "tool": "clean_code",
  "arguments": {
    "path": "src/main/kotlin/MyClass.kt",
    "formatCode": true,
    "optimizeImports": true,
    "runInspections": true
  }
}
```

#### 4. 库代码查看

**右键菜单操作：**

1. 在代码中右键点击第三方库的类或方法
2. 选择 **MCP Actions** → **View Library Code**
3. 查看源码或反编译结果

**MCP 工具调用：**

```json
{
  "tool": "view_lib_code",
  "arguments": {
    "filePath": "src/main/kotlin/MyClass.kt",
    "fullyQualifiedName": "com.example.ThirdPartyClass",
    "memberName": "someMethod"
  }
}
```

---

## 🏗️ 开发指南

### 🔧 开发环境搭建

#### 1. 克隆项目

```bash
git clone https://github.com/TrueNine/compose-server.git
cd compose-server/ide/ide-idea-mcp
```

#### 2. 导入项目

1. 打开 IntelliJ IDEA
2. 选择 **Open** 并导入项目根目录
3. 等待 Gradle 同步完成

#### 3. 配置 SDK

1. 进入 **File** → **Project Structure** → **SDKs**
2. 添加 **IntelliJ Platform Plugin SDK**
3. 设置 SDK 路径为您的 IDEA 安装目录

#### 4. 运行插件

```bash
# 在开发环境中运行插件
./gradlew runIde

# 构建插件
./gradlew buildPlugin

# 运行测试
./gradlew test

# 验证插件
./gradlew verifyPlugin
```

### 📝 代码规范

#### Kotlin 代码风格

- **缩进**：使用 2 个空格
- **行长度**：最大 160 字符
- **命名规范**：
  - 类名：PascalCase
  - 函数名：camelCase
  - 常量：UPPER_SNAKE_CASE
- **注释规范**：
  - 文档注释使用英文
  - 内部注释使用中文

#### 项目结构

```
ide/ide-idea-mcp/
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   └── io/github/truenine/composeserver/ide/ideamcp/
│   │   │       ├── tools/          # MCP 工具实现
│   │   │       ├── services/       # 核心服务
│   │   │       ├── ui/             # UI 组件
│   │   │       ├── actions/        # 右键菜单动作
│   │   │       └── infrastructure/ # 基础设施
│   │   └── resources/
│   │       ├── META-INF/
│   │       │   └── plugin.xml      # 插件配置
│   │       └── icons/              # 图标资源
│   └── test/
│       └── kotlin/                 # 测试代码
├── build.gradle.kts                # 构建配置
└── README.md                       # 项目文档
```

#### 测试规范

- **单元测试覆盖率**：目标 80% 以上
- **测试命名**：使用描述性的测试方法名
- **Mock 使用**：使用 MockK 进行依赖模拟
- **集成测试**：使用 IDEA 测试框架

```kotlin
class TerminalServiceTest {
  @Test
  fun `should execute command successfully with clean output`() {
    // Given
    val command = "gradle build"
    val expectedOutput = "BUILD SUCCESSFUL"

    // When
    val result = terminalService.executeCommand(project, command)

    // Then
    assertThat(result.exitCode).isEqualTo(0)
    assertThat(result.output).contains(expectedOutput)
  }
}
```

### 🔄 贡献流程

1. **Fork 项目**：在 GitHub 上 Fork 项目仓库
2. **创建分支**：创建功能分支 `git checkout -b feature/your-feature`
3. **开发功能**：按照代码规范开发新功能
4. **编写测试**：为新功能编写单元测试和集成测试
5. **提交代码**：提交代码并推送到您的 Fork
6. **创建 PR**：创建 Pull Request 并描述您的更改
7. **代码审查**：等待代码审查和反馈
8. **合并代码**：审查通过后合并到主分支

### 📊 性能优化

- **异步处理**：使用 Kotlin Coroutines 处理长时间运行的操作
- **内存管理**：实现日志轮转和缓存清理机制
- **UI 响应性**：避免在 EDT 线程中执行耗时操作
- **资源管理**：及时释放文件句柄和网络连接

---

## 🧪 测试

### 🔬 测试类型

#### 单元测试

```bash
# 运行所有单元测试
./gradlew test

# 运行特定测试类
./gradlew test --tests "TerminalServiceTest"

# 生成测试报告
./gradlew test jacocoTestReport
```

#### 集成测试

```bash
# 运行集成测试
./gradlew integrationTest

# 运行 UI 测试
./gradlew uiTest
```

#### 性能测试

```bash
# 运行性能测试
./gradlew performanceTest
```

### 📈 测试覆盖率

当前测试覆盖率：**85%+**

- **工具层**：90%+
- **服务层**：85%+
- **UI 层**：75%+
- **基础设施层**：90%+

---

## 📚 API 文档

### 🔧 MCP 工具 API

#### TerminalTool

```kotlin
/**
 * 执行终端命令并返回清洗后的输出
 */
suspend fun execute(args: TerminalArgs): McpResponse<TerminalOutput>
```

#### ViewErrorTool

```kotlin
/**
 * 查看文件或文件夹中的错误信息
 */
suspend fun execute(args: ViewErrorArgs): McpResponse<ErrorReport>
```

#### CleanCodeTool

```kotlin
/**
 * 清理和格式化代码
 */
suspend fun execute(args: CleanCodeArgs): McpResponse<CleanReport>
```

#### ViewLibCodeTool

```kotlin
/**
 * 查看第三方库源代码
 */
suspend fun execute(args: ViewLibCodeArgs): McpResponse<LibCodeResult>
```

### 📖 详细 API 文档

- **[在线 API 文档](https://javadoc.io/doc/io.github.truenine/composeserver-ide-idea-mcp)** - 完整的 API 文档
- **[插件开发指南](https://plugins.jetbrains.com/docs/intellij/welcome.html)** - IDEA 插件开发官方文档

---

## 🤝 社区和支持

### 💬 获取帮助

- **[GitHub Issues](https://github.com/TrueNine/compose-server/issues)** - 报告 Bug 和功能请求
- **[GitHub Discussions](https://github.com/TrueNine/compose-server/discussions)** - 社区讨论和问答
- **[项目文档](https://github.com/TrueNine/compose-server/wiki)** - 详细的项目文档

### 🎯 贡献方式

- **代码贡献**：提交 Pull Request
- **文档改进**：完善文档和示例
- **Bug 报告**：报告发现的问题
- **功能建议**：提出新功能想法
- **测试反馈**：提供使用反馈

### 📊 项目统计

- **GitHub Stars**：⭐ 关注项目获取最新动态
- **Contributors**：👥 感谢所有贡献者
- **Issues**：🐛 积极处理用户反馈
- **Pull Requests**：🔄 欢迎社区贡献

---

## 📄 许可证

本项目采用 [LGPL 2.1](../../LICENSE) 许可证。

---

## 🔗 相关链接

### 📚 **技术文档**

- **[IntelliJ Platform Plugin SDK](https://plugins.jetbrains.com/docs/intellij/welcome.html)** - IDEA 插件开发官方文档
- **[Model Context Protocol](https://modelcontextprotocol.io/)** - MCP 协议官方网站
- **[Kotlin MCP SDK](https://github.com/modelcontextprotocol/kotlin-sdk)** - Kotlin MCP 开发工具包
- **[MCP Specification](https://spec.modelcontextprotocol.io/)** - MCP 协议技术规范

### 🛠️ **开发工具**

- **[JetBrains Plugin Repository](https://plugins.jetbrains.com/)** - JetBrains 插件市场
- **[IntelliJ Platform Explorer](https://plugins.jetbrains.com/intellij-platform-explorer/)** - 平台 API 浏览器
- **[Plugin DevKit](https://plugins.jetbrains.com/docs/intellij/plugin-development-guidelines.html)** - 插件开发工具包

### 🎯 **示例项目**

- **[MCP Server Examples](https://github.com/modelcontextprotocol/servers)** - MCP 服务器实现示例
- **[JetBrains MCP Client](https://github.com/JetBrains/mcp-jetbrains)** - JetBrains MCP 客户端
- **[Plugin Development Samples](https://github.com/JetBrains/intellij-sdk-code-samples)** - IDEA 插件开发示例

---

<div align="center">

**🚀 让 AI 助力您的开发工作流程！**

[⭐ Star on GitHub](https://github.com/TrueNine/compose-server) •
[📖 Documentation](https://github.com/TrueNine/compose-server/wiki) •
[🐛 Report Issues](https://github.com/TrueNine/compose-server/issues) •
[💬 Discussions](https://github.com/TrueNine/compose-server/discussions)

</div>
