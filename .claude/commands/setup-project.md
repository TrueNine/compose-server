---
allowed-tools: WebSearch, WebFetch, Read, Write, Edit, MultiEdit, Glob, Grep, Bash, TodoWrite, Task
description: Complete Claude Code project setup with MCP tools and configuration
---

# Claude Code Project Setup Assistant

This command provides comprehensive setup instructions for Claude Code projects, including MCP (Model Context Protocol) tools installation and configuration management.

## MCP Tools Installation

Execute the following commands in your project root directory to install essential MCP tools.

### Linux/macOS Commands

```bash
# Context7 - Access up-to-date documentation for any library
claude mcp add context7 --scope project -- npx -y @upstash/context7-mcp@latest

# Grep - Search millions of GitHub repositories for code examples
# Reference: https://vercel.com/blog/grep-a-million-github-repositories-via-mcp
claude mcp add --scope project --transport http grep https://mcp.grep.app

# DeepWiki - GitHub repository documentation assistant
# Reference: https://docs.devin.ai/work-with-devin/deepwiki-mcp
claude mcp add --scope project --transport http deepwiki https://mcp.deepwiki.com/mcp

# Sequential Thinking - Enhanced reasoning and problem-solving
claude mcp add sequential-thinking --scope project -- npx -y @modelcontextprotocol/server-sequential-thinking

# Memory - Knowledge graph for persistent context
# Reference: https://github.com/modelcontextprotocol/servers/tree/main/src/memory
claude mcp add memory --scope project -- npx -y @modelcontextprotocol/server-memory

# Playwright - Web automation and testing
# Reference: https://github.com/microsoft/playwright-mcp
claude mcp add playwright --scope project -- npx -y @playwright/mcp@latest --viewport-size 1920,1080
```

### Windows Commands

```bash
# Context7 - Access up-to-date documentation for any library
claude mcp add-json context7 --scope project '{"command":"cmd","args":["/c","npx","-y","@upstash/context7-mcp@latest"]}'

# Grep - Search millions of GitHub repositories for code examples
claude mcp add --scope project --transport http grep https://mcp.grep.app

# DeepWiki - GitHub repository documentation assistant
claude mcp add --scope project --transport http deepwiki https://mcp.deepwiki.com/mcp

# Sequential Thinking - Enhanced reasoning and problem-solving
claude mcp add-json sequential-thinking --scope project '{"command":"cmd","args":["/c","npx","-y","@modelcontextprotocol/server-sequential-thinking@latest"]}'

# Memory - Knowledge graph for persistent context
claude mcp add-json memory --scope project '{"command":"cmd","args":["/c","npx","-y","@modelcontextprotocol/server-memory@latest"]}'

# Playwright - Web automation and testing
claude mcp add-json playwright --scope project '{"command":"cmd","args":["/c","npx","-y","@playwright/mcp@latest","--viewport-size","1920,1080"]}'
```

## Claude Code Directory Structure

Understanding the Claude Code file organization:

```text
# Global Configuration
~/.claude/                      # Global Claude Code directory
~/.claude.json                  # Global settings
~/.claude.json.backup          # Backup of global settings

# Project-Specific Configuration
project/.claude/               # Project Claude Code directory
project/.claude/settings.json        # Project settings
project/.claude/settings.local.json  # Local project settings (gitignored)
project/.claude/.mcp.json           # MCP configuration
project/.claude/.mcp.local.json     # Local MCP configuration (gitignored)
```

## Troubleshooting

### Clear Caches and Reset

If experiencing issues, remove these cache directories and files:

```text
# Global Caches
~/.claude/tools/              # Cached tools
~/.claude/projects/           # Project caches
~/.claude/settings.local.json # Local settings
~/.claude.json               # Global config
~/.claude.json.backup        # Config backup

# Project Caches
project/.claude/settings.local.json # Local project settings
project/.mcp.json                   # MCP config
project/.mcp.local.json            # Local MCP config
```

### Fix Onboarding Issues

If Claude Code fails to start, manually set the onboarding flag in `~/.claude.json`:

```json
{
  "hasCompletedOnboarding": true
}
```

## Post-Setup Verification

After installation, verify your setup by:
1. Checking MCP tools are properly installed: `claude mcp list`
2. Testing agent functionality with sub-agents
3. Running a simple command to ensure all tools work correctly

## Usage Tips

- Use `--scope project` to install tools project-wide
- Local configuration files are automatically gitignored
- MCP tools extend Claude's capabilities significantly
- Each tool serves specific purposes - refer to their documentation for advanced usage