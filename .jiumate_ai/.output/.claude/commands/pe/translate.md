---
argument-hint: [ locale_markdown_file ] [ translation_description ]
allowed-tools: Read, Write, Glob, Grep, Bash
description: Translate Chinese localization memory prompt file to English memory prompt file, maintaining consistent terminology and quality standards
---

Translate Chinese localization memory prompt file #$1 (.locale.md) to English memory prompt file, while maintaining quality standards and terminology consistency.

# Task Execution Workflow
## [STEP-1] **Parse output path**:
**Priority match special paths**, generate target files according to the following mapping:

| Source file path                                          | Output file path                                                                      |
|-----------------------------------------------------------|---------------------------------------------------------------------------------------|
| `.jiumate_ai/.locale/**/*.locale.md`                      | `**/*.md`                                                                             |
| `.jiumate_ai/.locale/**/AGENTS.locale.md`                 | `**/AGENTS.md`, `**/CLAUDE.md`                                                        |
| `.jiumate_ai/.locale/AGENTS.locale.md`                   | `AGENTS.md`, `CLAUDE.md`                                                              |
| `.jiumate_ai/.locale/README.locale.md`                   | `README.md`                                                                           |
| `.jiumate_ai/cmd/**/*.locale.md`                          | `.claude/commands/**/*.md`, `.jiumate_ai/.output/.claude/commands/**/*.md`           |
| `.jiumate_ai/sa/**/*.locale.md`                           | `.claude/subagents/**/*.md`, `.jiumate_ai/.output/.claude/subagents/**/*.md`         |
| `.jiumate_ai/user/**/*.locale.md`                         | `~/.claude/CALUDE.md`, `~/.codex/AGENTS.md`,`.jiumate_ai/.output/GLOBAL/**.md`         |

**When special paths don't match**, use general rule: `filename.locale.extension` -> `filename.extension`

## [STEP-2] **Check target file**:
- Use `Glob(pattern: "<target_file>")` to verify target file exists
- Use `Bash(command: "mkdir <target_directory>"` to create all directories that should exist
- Pattern: Based on target path determined in [STEP-1]

## [STEP-3] **Delete existing file**:
- WHEN target file exists THEN use `Bash(command: "rm <target_file>")` tool to delete
- Command: `Bash(command: "rm <target_file>")` (Linux/Mac) or equivalent (Windows) command

## [STEP-4] **Read source file**: `Read($1)`

## [STEP-5] **Execute translation**:
- Preserve `Markdown` structure and formatting
- Keep code blocks unchanged, translate all comment content within code blocks

## [STEP-6] **Write target file**:
- Create new target file and write translated content
- WHEN multiple output target files exist THEN output first target file first, then call `Bash(command: "cp -R <first_file> <target_file>")` to copy directly to ensure accuracy
- No need to read existing target file (already deleted in [STEP-4])

## [STEP-7] **Error handling**:
- WHEN `Write` call fails THEN immediately `Bash(command: "rm <target_file>")` target file
- Use `Bash(command: "rm <target_file")` to execute deletion
- Restart process without attempting to fix




# Quality Standards
- **Terminology consistency**: Determine translation by comparing with glossary item by item, maintain consistent capitalization and punctuation with terminology table
- **Technical accuracy**: Confirm commands, parameters, file paths and other technical information are correct, avoid introducing new meanings
- **Format preservation**: Preserve title hierarchy, list indentation, tables and inline code as-is, do not add or remove blank lines
- **Whitespace preservation**: Strictly preserve blank lines and spaces, they are also part of the prompt
- **Link handling**: Update relative/absolute paths according to target document structure, ensure anchors are synchronized with filenames
- **Code integrity**: Keep code block content unchanged, only translate comments within blocks and verify statement alignment

```xml
<Examples description="File path conversion">
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