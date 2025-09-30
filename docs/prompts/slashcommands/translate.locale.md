---
argument-hint: [locale_markdown_file] [translation_description]
allowed-tools: Read, Write, Glob, Grep, Bash
description: 将中文本地化记忆提示词文件翻译为英文记忆提示词文件，遵循一致的术语和质量标准
---

将中文本地化记忆提示词文件 #$1 (.locale.md) 翻译为英文记忆提示词文件，同时保持质量标准和术语一致性。

# 关键要求

## 任务执行流程
1. **读取源文件**: `Read($1)`
2. **解析文件名**:
  - **特殊位置规则** (优先检查):
    - `docs/prompts/slashcommands/**.locale.md` → `.claude/commands/**.md`
    - `docs/CLAUDE-prompts-slashcommands.locale.md` → `docs/prompts/slashcommands/CLAUDE.md`
    - `docs/CLAUDE-prompts.locale.md` → `docs/prompts/CLAUDE.md`
    - `docs/CLAUDE-prompts-user.locale.md` → `docs/prompts/user/CLAUDE.md`
    - `docs/CLAUDE-prompts-project.locale.md` → `docs/prompts/project/CLAUDE.md`
    - `docs/CLAUDE-qa.locale.md` → `docs/qa/CLAUDE.md`
    - `docs/CLAUDE-references.locale.md` → `docs/references/CLAUDE.md`
  - **标准规则**: `filename.locale.extension` → `filename.extension`
3. **检查目标文件**:
  - 使用 `Search(pattern: "$1")` 验证目标文件是否存在
  - 模式: 基于步骤 2 确定的目标路径
4. **删除现有文件**:
  - 如果目标文件存在，使用 Bash 工具删除
  - 命令: `Bash(rm filename.extension)` (Linux/Mac) 或 等价 (Windows) 命令
5. **执行翻译**:
  - 保留 Markdown 结构和格式
  - 应用词汇表中的一致术语
  - 保持代码块不变, 且翻译所有注释内容
  - 保持 `<BadExample>` 中的例子内容不变
6. **写入目标文件**:
  - 创建新的目标文件并写入翻译内容
  - 无需读取现有目标文件 (已在步骤 4 中删除)
7. **错误处理**:
  - 如果 `Write` 工具失败，立即删除目标文件
  - 使用 `Bash` 工具执行删除
  - 重新开始流程，不尝试修复

> user: $2

## 质量标准
- **术语一致性**: 严格遵循词汇表
- **技术准确性**: 保持技术概念的精确性
- **格式保持**: 保留所有 Markdown 格式
- **链接处理**: 适当更新文档链接
- **代码完整性**: 保持代码示例不变

<Examples>
<Example description="文件名转换示例">
- `translate.locale.md` → `translate.md`
- `setup.locale.md` → `setup.md`
- `config.locale.yaml` → `config.yaml`
</Example>

<Examples>
<GoodExample description="正确的翻译方法">
user: 请参阅文档
claude: See documentation

user: 配置文件
claude: Configuration file

user: 命令行工具
claude: Command-line tool
</GoodExample>
</Examples>

# 核心术语

## 常用术语
- 请参阅/参见 - see, refer to
- 配置 - configuration, config
- 设置 - settings
- 文档 - documentation, docs
- 指南 - guide
- 教程 - tutorial
- 示例 - example
- 工具 - tool
- 命令 - command
- 脚本 - script
- 文件 - file
- 目录 - directory
- 路径 - path

## Claude Code 术语
- 钩子 - hook
- 斜杠命令 - slash command
- 工作区 - workspace
- 代理 - agent
- 模型 - model
- 提示 - prompt
- 上下文 - context
- 会话 - session
- 任务 - task
- 工作流 - workflow
