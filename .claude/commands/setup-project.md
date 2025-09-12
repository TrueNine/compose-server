---
allowed-tools: WebSearch, WebFetch, Read, Write, Edit, MultiEdit, Glob, Grep, Bash, TodoWrite, Task
description: setup a this project.
---


# setup claude code mcp

在 root project 下执行如下命令

```bash
claude mcp add -s project context7 npx -- -y @upstash/context7-mcp@latest

# see https://vercel.com/blog/grep-a-million-github-repositories-via-mcp
claude mcp add -s project --transport http grep https://mcp.grep.app

claude mcp add -s project sequential-thinking npx -- -y @modelcontextprotocol/server-sequential-thinking

# see https://github.com/modelcontextprotocol/servers/tree/main/src/memory
claude mcp add -s project memory npx -- -y @modelcontextprotocol/server-memory
```
