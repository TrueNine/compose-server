---
argument-hint: [ locale_markdown_file ] [ translation_description ]
allowed-tools: Read, Write, Glob, Grep, Bash
description: 将中文本地化记忆提示词文件翻译为英文记忆提示词文件，遵循一致的术语和质量标准
---

将中文本地化记忆提示词文件 #$1 (.locale.md) 翻译为 英文 记忆提示词文件, 同时保持 质量标准 和 术语一致性.

# 任务执行流程
## [STEP-1] **解析输出路径**：
**优先匹配特殊路径**，按照下列映射生成目标文件：

| 源文件路径                                     | 输出文件路径                                                                         |
|-------------------------------------------|--------------------------------------------------------------------------------|
| `.jiumate_ai/.locale/**/*.locale.md`      | `**/*.md`                                                                      |
| `.jiumate_ai/.locale/**/AGENTS.locale.md` | `**/AGENTS.md`, `**/CLAUDE.md`                                                 |
| `.jiumate_ai/.locale/AGENTS.locale.md`    | `AGENTS.md`, `CLAUDE.md`                                                       |
| `.jiumate_ai/.locale/README.locale.md`    | `README.md`                                                                    |
| `.jiumate_ai/cmd/**/*.locale.md`          | `.claude/commands/**/*.md`, `.jiumate_ai/.output/.claude/commands/**/*.md`     |
| `.jiumate_ai/sa/**/*.locale.md`           | `.claude/subagents/**/*.md`, `.jiumate_ai/.output/.claude/subagents/**/*.md`   |
| `.jiumate_ai/user/**/*.locale.md`         | `~/.claude/CALUDE.md`, `~/.codex/AGENTS.md`,`.jiumate_ai/.output/GLOBAL/**.md` |

**未命中特殊路径时**，使用通用规则：`filename.locale.extension` -> `filename.extension`

## [STEP-2] **检查目标文件**:
- 使用 `Search(pattern: "<target_file>")` 验证目标文件是否存在
- 使用 `Bash(command: "mkdir <target_directory>"` 创建所有应存在的目录
- 模式: 基于 [STEP-1] 确定的目标路径

## [STEP-3] **删除现有文件**:
- WHEN 目标文件存在 THEN 使用 `Bash(command: "rm <target_file>")` 工具删除
- 命令: `Bash(command: "rm <target_file>")` (Linux/Mac) 或 等价 (Windows) 命令

## [STEP-4] **读取源文件**: `Read($1)`

## [STEP-5] **执行翻译**:
- 保留 `Markdown` 结构和格式
- 保持代码块不变, 翻译代码块中所有注释内容

## [STEP-6] **写入目标文件**:
- 创建新的目标文件并写入翻译内容
- WHEN 存在多个输出目标文件 THEN 先输出第一份目标文件, 随后调用 `Bash(command: "cp -R <first_file> <target_file>")` 直接复制以保证准确性
- 无需读取现有目标文件 (已在 [STEP-4] 中删除)

## [STEP-7] **错误处理**:
- WHEN 调用 `Write` 失败 THEN 立即 `Bash(command: "rm <target_file>")` 目标文件
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
    .jiumate_ai/cmd/translate.locale.md -> [.claude/commands/translate.md, .jiumate_ai/.output/.claude/commands/translate.md]
  </Example>
  <Example>
    .jiumate_ai/cmd/setup.locale.md` -> [.claude/commands/setup.md, .jiumate_ai/.output/.claude/commands/setup.md]
  </Example>
  <Example>
    .jiumate_ai/.locale/AGENTS.locale.md -> [AGENTS.md, CLAUDE.md]
  </Example>
  <Example>
    .jiumate_ai/.locale/README.locale.md -> README.md
  </Example>
  <Example>
    .jiumate_ai/.locale/.jiumate_ai/cmd/AGENTS.locale.md -> [.jiumate_ai/cmd/AGENTS.md, .jiumate_ai/cmd/CLAUDE.md]
  </Example>
</Examples>
```
