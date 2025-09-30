---
allowed-tools: Read, Write, Edit, MultiEdit, Glob, Grep, Bash, TodoWrite, Task
description: Child project Claude configuration copy process, copying root directory .claude/ configuration files to current working directory
---

# Child-Use Configuration Copy Process

Copy root directory `.claude/` configuration to current cwd for child project Claude environment initialization.

## Core Requirements

### Copy Targets
- `.claude/commands/**`
- `.claude/agents/**` (corresponding to subagent)
- `.claude/.gitignore`

### Forbidden to Copy
- `.claude/settings.json`
- `.claude/settings.local.json`

### Operation Constraints
- Forbidden to read file contents, only use `ls` to check existence
- If target `.claude/` exists, delete and rebuild directly
- Finally create `.claude/.gitignore` with content `*`

## Process Design

### 1. Environment Cleanup
```bash
if [ -d ".claude" ]; then
    rm -rf .claude
fi
```

### 2. Source File Detection
```bash
ROOT_DIR=$(find_root_with_claude)
ls "$ROOT_DIR/.claude/commands/" >/dev/null 2>&1
ls "$ROOT_DIR/.claude/agents/" >/dev/null 2>&1
ls "$ROOT_DIR/.claude/.gitignore" >/dev/null 2>&1
```

### 3. File Copy
```bash
mkdir -p .claude

if [ -d "$ROOT_DIR/.claude/commands" ]; then
    cp -r "$ROOT_DIR/.claude/commands" .claude/
fi

if [ -d "$ROOT_DIR/.claude/agents" ]; then
    cp -r "$ROOT_DIR/.claude/agents" .claude/
fi

if [ -f "$ROOT_DIR/.claude/.gitignore" ]; then
    cp "$ROOT_DIR/.claude/.gitignore" .claude/.gitignore.backup
fi
```

### 4. Configuration Override
```bash
echo "*" > .claude/.gitignore
```

## Features

- Search upward for root directory
- Atomic operations
- Settings file protection
