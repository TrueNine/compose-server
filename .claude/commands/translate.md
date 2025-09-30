---
argument-hint: [locale_markdown_file] [translation_description]
allowed-tools: Read, Write, Glob, Grep, Bash
description: Translate Chinese localization memory prompt file to English memory prompt file while maintaining quality standards and terminology consistency
---

Translate Chinese localization memory prompt file #$1 (.locale.md) to English memory prompt file while maintaining quality standards and terminology consistency.

# Key Requirements

## Task Execution Workflow
1. **Read source file**: `Read($1)`
2. **Parse filename**:
  - **Special location rules** (Check first):
    - `docs/prompts/slashcommands/**.locale.md` → `.claude/commands/**.md`
    - `docs/CLAUDE-prompts-slashcommands.locale.md` → `docs/prompts/slashcommands/CLAUDE.md`
    - `docs/CLAUDE-prompts.locale.md` → `docs/prompts/CLAUDE.md`
    - `docs/CLAUDE-prompts-user.locale.md` → `docs/prompts/user/CLAUDE.md`
    - `docs/CLAUDE-prompts-project.locale.md` → `docs/prompts/project/CLAUDE.md`
    - `docs/CLAUDE-qa.locale.md` → `docs/qa/CLAUDE.md`
    - `docs/CLAUDE-references.locale.md` → `docs/references/CLAUDE.md`
  - **Standard rule**: `filename.locale.extension` → `filename.extension`
3. **Check target file**:
  - Use `Glob(pattern: "$1")` to verify if target file exists
  - Pattern: Based on target path determined in step 2
4. **Delete existing file**:
  - If target file exists, use Bash tool to delete
  - Command: `Bash(rm filename.extension)` (Linux/Mac) or equivalent (Windows) command
5. **Perform translation**:
  - Preserve Markdown structure and formatting
  - Apply consistent terminology from glossary
  - Keep code blocks unchanged and translate all comment content
  - Keep example content in `<BadExample>` unchanged
6. **Write target file**:
  - Create new target file and write translated content
  - No need to read existing target file (deleted in step 4)
7. **Error handling**:
  - If Write tool fails, immediately delete target file
  - Use Bash tool to execute deletion
  - Restart process without attempting to fix

> user: $2

## Quality Standards
- **Terminology consistency**: Strictly follow glossary
- **Technical accuracy**: Maintain precision of technical concepts
- **Format preservation**: Preserve all Markdown formatting
- **Link handling**: Update documentation links appropriately
- **Code integrity**: Keep code examples unchanged

<Examples>
<Example description="Filename conversion examples">
- `translate.locale.md` → `translate.md`
- `setup.locale.md` → `setup.md`
- `config.locale.yaml` → `config.yaml`
</Example>

<Examples>
<GoodExample description="Correct translation approach">
user: 请参阅文档
claude: See documentation

user: 配置文件
claude: Configuration file

user: 命令行工具
claude: Command-line tool
</GoodExample>
</Examples>

# Core Terminology

## Common Terms
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

## Claude Code Terms
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