---
allowed-tools: WebSearch, WebFetch, Read, Write, Edit, MultiEdit, Glob, Grep, Bash, TodoWrite, Task
description: setup a this project.
---

# setup claude code to project

## install mcp tools

在 root project 下执行如下命令

### linux

```bash
claude mcp add context7 --scope project -- npx -y @upstash/context7-mcp@latest

# see https://vercel.com/blog/grep-a-million-github-repositories-via-mcp
claude mcp add --scope project --transport http grep https://mcp.grep.app

claude mcp add sequential-thinking --scope project -- npx -y @modelcontextprotocol/server-sequential-thinking

# see https://github.com/modelcontextprotocol/servers/tree/main/src/memory
claude mcp add memory --scope project -- npx -y @modelcontextprotocol/server-memory
```

### windows

```bash
claude mcp add-json context7 --scope project '{\"command\":\"cmd\",\"args\":[\"/c\",\"npx\",\"-y\",\"@upstash/context7-mcp@latest\"]}'

# see https://vercel.com/blog/grep-a-million-github-repositories-via-mcp
claude mcp add --scope project --transport http grep https://mcp.grep.app

claude mcp add-json sequential-thinking --scope project '{\"command\":\"cmd\",\"args\":[\"/c\",\"npx\",\"-y\",\"@modelcontextprotocol/server-sequential-thinking@latest\"]}'

# see https://github.com/modelcontextprotocol/servers/tree/main/src/memory
claude mcp add-json memory --scope project '{\"command\":\"cmd\",\"args\":[\"/c\",\"npx\",\"-y\",\"@modelcontextprotocol/server-memory@latest\"]}'

```

## claude code structure

```text
~/.claude/
~/.claude.json
~/.claude.json.backup

project/.claude/
```

### clean caches

```text
~/.claude/tools/
~/.claude/projects/
~/.claude.json
~/.claude.json.backup
```

### configuration .claude.json hasCompletedOnboarding field

无法启动,则在 `~/.claude.json`

```json
{
  "hasCompletedOnboarding": true
}
```
