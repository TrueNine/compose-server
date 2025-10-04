---
argument-hint: [ locale_markdown_file ] [ translation_description ]
allowed-tools: Read, Write, Glob, Grep, Bash
description: 将中文本地化记忆提示词文件翻译为英文记忆提示词文件，遵循一致的术语和质量标准
---

将中文本地化记忆提示词文件 #$1 (.locale.md) 翻译为 英文 记忆提示词文件, 同时保持 质量标准 和 术语一致性.

# 任务执行流程
## [STEP-1] **解析文件名**：
- **优先匹配特殊路径**，按照下列映射生成目标文件：
  - `.docs/cmd/**/*.locale.md` -> `.claude/commands/**/*.md`
  - `.docs/sa/**/*.locale.md` -> `.claude/agents/***/*.md`
  - `.docs/AGENTS-cmd.locale.md` -> [`.docs/cmd/AGENTS.md`, `.docs/cmd/CLAUDE.md`]
  - `.docs/AGENTS-sa.locale.md` -> [`.docs/sa/AGENTS.md`, `.docs/sa/CLAUDE.md`]
  - `.docs/AGENTS-user.locale.md` -> [`.docs/user/AGENTS.md`, `.docs/user/CLAUDE.md`]
  - `.docs/AGENTS-project.locale.md` -> [`.docs/project/AGENTS.md`, `.docs/project/AGENTS.md`]
  - `.docs/AGENTS.locale.md` -> [`.docs/AGENTS.md`, `.docs/CLAUDE.md`]
  - `AGENTS.locale.md` -> [`AGENTS.md`, `CLAUDE.md`]
  - `README.locale.md` -> `README.md`
- **未命中特殊路径时**，使用通用规则：`filename.locale.extension` -> `filename.extension`

## [STEP-2] **检查目标文件**:
- 使用 `Search(pattern: "target_file")` 验证目标文件是否存在
- 模式: 基于 [STEP-1] 确定的目标路径

## [STEP-3] **删除现有文件**:
- 如果目标文件存在，使用 `Bash(command: "rm <target_file>")` 工具删除
- 命令: `Bash(command: "rm <target_file>")` (Linux/Mac) 或 等价 (Windows) 命令

## [STEP-4] **读取源文件**: `Read($1)`

## [STEP-5] **执行翻译**:
- 保留 `Markdown` 结构和格式
- 保持代码块不变, 翻译代码块中所有注释内容

## [STEP-6] **写入目标文件**:
- 创建新的目标文件并写入翻译内容
- 无需读取现有目标文件 (已在 [STEP-4] 中删除)

## [STEP-7] **错误处理**:
- 如果 `Write` 失败，立即 `Bash(command: "rm <target_file>")` 目标文件
- 使用 `Bash(command: "rm <target_file")` 执行删除
- 重新开始流程，不尝试修复





# 质量标准
- **术语一致性**: 逐条对照词汇表确定译法，保持大小写和标点与术语表一致
- **技术准确性**: 确认命令、参数、文件路径等技术信息无误，避免引入新的含义
- **格式保持**: 原样保留标题层级、列表缩进、表格与内联代码，不新增或删减空行
- **空白字符保留**: 严格保留空行以及空格，它们也是提示词中的一部分
- **链接处理**: 按目标文档结构更新相对/绝对路径，确保锚点与文件名同步
- **代码完整性**: 保持代码块内容不变，仅翻译块内注释并核对语句对齐

```xml
<Examples description="文件路径转换">
  <Example>
    translate.locale.md -> translate.md
  </Example>
  <Example>
    setup.locale.md` -> setup.md
  </Example>
  <Example>
    AGENTS.locale.md -> [AGENTS.md, CLAUDE.md]
  </Example>
</Examples>
```
