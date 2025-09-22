---
allowed-tools: Read, Write, Edit, MultiEdit, Glob, Grep, TodoWrite, Task
description: Professional documentation organization and optimization for Claude Code projects
---

# Documentation Organization Assistant

This command provides comprehensive documentation management for Claude Code projects, ensuring professional structure, consistency, and adherence to naming conventions.

## Project Documentation Structure

```text
.claude/
├── commands/**/*.md          # Claude Code Slash Command Files
└── agents/**/*.md           # Claude Code Sub Agent Files

docs/
├── prompts/                 # Claude Code Memory Prompts
│   ├── locale/*.md         # Localized memory files (user-written)
│   └── output/*.md         # Files to be translated to English
└── specs/                  # Specification-driven development documents
```

## Core Responsibilities

### Documentation Quality Assurance
- **File Optimization**: Enhance clarity, structure, and professionalism of all documentation
- **Content Cleanup**: Remove redundant information and improve readability
- **Naming Standards**: Ensure all Markdown files follow `[a-zA-Z-]*.md` pattern
- **Language Processing**: Translate and standardize content as needed

### Claude Code File Management
- **Memory Files**: Optimize and translate Claude Code memory prompts
- **Slash Commands**: Enhance command files for better functionality and clarity
- **Sub Agents**: Refine agent specifications for improved performance
- **Standard Compliance**: Ensure all Claude Code files meet official specifications

### Content Translation & Localization
- **English Translation**: Convert locale-specific content to English standards
- **Consistency Checks**: Maintain uniform terminology and formatting
- **Cultural Adaptation**: Ensure content is appropriate for international use

## Operational Guidelines

### File Handling Protocol
- **Output Files**: Never use `Read` tool on `docs/prompts/output/**/*.md`
- **Replacement Strategy**: Directly replace existing files or use replacement tools from start
- **Generation Policy**: Treat each generation as creating new files

### Quality Standards
- **Professional Tone**: Maintain formal, technical writing style
- **Structural Consistency**: Use standardized headers, formatting, and organization
- **Accuracy Priority**: Ensure technical accuracy over stylistic preferences
- **Completeness**: Verify all sections are properly documented

## Usage Instructions

This command will:
1. **Scan Documentation**: Analyze current file structure and content quality
2. **Identify Issues**: Find naming violations, content gaps, and inconsistencies
3. **Apply Fixes**: Correct naming, improve content, and ensure compliance
4. **Optimize Structure**: Reorganize content for better accessibility and maintenance
5. **Validate Results**: Confirm all changes meet professional standards

## Best Practices

- **Incremental Updates**: Process documentation in logical chunks
- **Version Control**: Maintain compatibility with existing workflows
- **User Experience**: Prioritize clarity and usability for end users
- **Documentation Standards**: Follow industry best practices for technical writing
- **Maintenance**: Ensure long-term sustainability of documentation structure
