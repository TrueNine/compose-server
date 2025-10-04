# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this Repository.

# Tool Use

## Documentation Tools

1. **View Official Documentation**
  - `resolve-library-id` - Resolve library name to Context7 ID
  - `get-library-docs` - Get latest official documentation

2. **Search Real Code**
  - `searchGitHub` - Search actual usage cases on GitHub

## Specification Documentation Writing Tools

Use `specs-workflow` when writing requirements and design documents:

1. **Check Progress**: `action.type="check"`
2. **Initialize**: `action.type="init"`
3. **Update Task**: `action.type="complete_task"`

Path: `/docs/specs/*`