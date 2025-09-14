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

# See https://docs.devin.ai/work-with-devin/deepwiki-mcp
claude mcp add --scope project --transport http deepwiki https://mcp.deepwiki.com/mcp

claude mcp add sequential-thinking --scope project -- npx -y @modelcontextprotocol/server-sequential-thinking

# see https://github.com/modelcontextprotocol/servers/tree/main/src/memory
claude mcp add memory --scope project -- npx -y @modelcontextprotocol/server-memory

# see # see https://github.com/microsoft/playwright-mcp
claude mcp add playwright --scope project -- npx -y @playwright/mcp@latest --viewport-size 1920,1080
```

### windows

```bash
claude mcp add-json context7 --scope project '{\"command\":\"cmd\",\"args\":[\"/c\",\"npx\",\"-y\",\"@upstash/context7-mcp@latest\"]}'

# see https://vercel.com/blog/grep-a-million-github-repositories-via-mcp
claude mcp add --scope project --transport http grep https://mcp.grep.app

# See https://docs.devin.ai/work-with-devin/deepwiki-mcp
claude mcp add --scope project --transport http deepwiki https://mcp.deepwiki.com/mcp

claude mcp add-json sequential-thinking --scope project '{\"command\":\"cmd\",\"args\":[\"/c\",\"npx\",\"-y\",\"@modelcontextprotocol/server-sequential-thinking@latest\"]}'

# see https://github.com/modelcontextprotocol/servers/tree/main/src/memory
claude mcp add-json memory --scope project '{\"command\":\"cmd\",\"args\":[\"/c\",\"npx\",\"-y\",\"@modelcontextprotocol/server-memory@latest\"]}'

# see https://github.com/microsoft/playwright-mcp
claude mcp add-json playwright --scope project '{\"command\":\"cmd\",\"args\":[\"/c\",\"npx\",\"-y\",\"@playwright/mcp@latest\",\"--viewport-size\",\"1920,1080\"]}'

```

## claude code structure

```text
~/.claude/
~/.claude.json
~/.claude.json.backup

project/.claude/
project/.claude/settings.json
project/.claude/settings.local.json
project/.claude/.mcp.json
project/.claude/.mcp.local.json
```

### clean caches

```text
~/.claude/tools/
~/.claude/projects/
~/.claude/settings.local.json
~/.claude.json
~/.claude.json.backup

project/.claude/settings.local.json
project/.mcp.json
project/.mcp.local.json
```

### configuration .claude.json hasCompletedOnboarding field

无法启动,则在 `~/.claude.json`

```json
{
  "hasCompletedOnboarding": true
}
```
