---
argument-hint: [ locale_markdown_file ] [ translation_description ]
allowed-tools: Read, Write, Glob, Grep, Bash
description: Translate Chinese localization memory prompt file to English memory prompt file, following consistent terminology and quality standards
---

Translate Chinese localization memory prompt file #$1 (.locale.md) to English memory prompt file while maintaining quality standards and terminology consistency.

# Task Execution Workflow

## [STEP-1] **Parse filename**:
- **Priority matching special paths**, generate target file according to the following mapping:
  - `.docs/cmd/**/*.locale.md` -> `.claude/commands/**/*.md`
  - `.docs/sa/**/*.locale.md` -> `.claude/agents/***/*.md`
  - `.docs/AGENTS-cmd.locale.md` -> [`.docs/cmd/AGENTS.md`, `.docs/cmd/CLAUDE.md`]
  - `.docs/AGENTS-sa.locale.md` -> [`.docs/sa/AGENTS.md`, `.docs/sa/CLAUDE.md`]
  - `.docs/AGENTS-user.locale.md` -> [`.docs/user/AGENTS.md`, `.docs/user/CLAUDE.md`]
  - `.docs/AGENTS-project.locale.md` -> [`.docs/project/AGENTS.md`, `.docs/project/AGENTS.md`]
  - `.docs/AGENTS.locale.md` -> [`.docs/AGENTS.md`, `.docs/CLAUDE.md`]
  - `AGENTS.locale.md` -> [`AGENTS.md`, `CLAUDE.md`]
  - `README.locale.md` -> `README.md`
- **When special paths don't match**, use general rule: `filename.locale.extension` -> `filename.extension`

## [STEP-2] **Check target file**:
- Use `Search(pattern: "target_file")` to verify if target file exists
- Pattern: Based on target path determined in [STEP-1]

## [STEP-3] **Delete existing file**:
- If target file exists, use `Bash(command: "rm <target_file>")` tool to delete
- Command: `Bash(command: "rm <target_file>")` (Linux/Mac) or equivalent (Windows) command

## [STEP-4] **Read source file**: `Read($1)`

## [STEP-5] **Execute translation**:
- Preserve `Markdown` structure and formatting
- Keep code blocks unchanged, translate all comment content within code blocks

## [STEP-6] **Write target file**:
- Create new target file and write translated content
- No need to read existing target file (already deleted in [STEP-4])

## [STEP-7] **Error handling**:
- If `Write` fails, immediately `Bash(command: "rm <target_file>")` target file
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
