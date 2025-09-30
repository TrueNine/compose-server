---
allowed-tools: Read, Write, Edit, MultiEdit, Glob, Grep, Bash, TodoWrite, Task
description: 子项目 Claude 配置复制流程，将根目录 .claude/ 配置文件复制到当前工作目录
---

# Child-Use 配置复制流程

将根目录 `.claude/` 配置复制到当前 cwd，用于子项目 Claude 环境初始化。

## 核心要求

### 复制目标
- `.claude/commands/**`
- `.claude/agents/**` (对应 subagent)
- `.claude/.gitignore`

### 禁止复制
- `.claude/settings.json`
- `.claude/settings.local.json`

### 操作约束
- 禁止读取文件内容，仅使用 `ls` 检查存在性
- 目标 `.claude/` 存在则直接删除重建
- 最终创建 `.claude/.gitignore`，内容为 `*`

## 流程设计

### 1. 环境清理
```bash
if [ -d ".claude" ]; then
    rm -rf .claude
fi
```

### 2. 源文件检测
```bash
ROOT_DIR=$(find_root_with_claude)
ls "$ROOT_DIR/.claude/commands/" >/dev/null 2>&1
ls "$ROOT_DIR/.claude/agents/" >/dev/null 2>&1
ls "$ROOT_DIR/.claude/.gitignore" >/dev/null 2>&1
```

### 3. 文件复制
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

### 4. 配置覆写
```bash
echo "*\n!.gitignore" > .claude/.gitignore
```

## 特点

- 向上查找根目录
- 原子性操作
- 设置文件保护
- 备份原 .gitignore
