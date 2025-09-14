---
allowed-tools: Read, Write, Edit, MultiEdit, Glob, Grep, TodoWrite, Task
description: organize this projects docs
---

## structure
- `.claude/commands/*` - Anthropic Claude Code Slash Command Files
- `docs/` - Documentations and Anthropic Claude Code Prompts
  + `docs/prompts/` - Anthropic Claude Code Memory Prompts
    - `docs/prompts/locale/*.md` - 用户编写的 本地化语言版本的 claude code memory.md 文件
    - `docs/prompts/output/*.md` - 需要被翻译为英文的 claude code memory 文件
  + `docs/specs/` - 规范驱动开发的文档文件

## work flow

1. 将 `docs/prompts/locale/*.md` 所有文件,以专业提示词工程师分身,将所有文件翻译为英文
2. 将翻译好的提示词文件逐个输出到对应的 `docs/prompts/output/*.md` 中
