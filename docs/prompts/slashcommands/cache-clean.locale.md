---
allowed-tools: Read, Write, Edit, MultiEdit, Glob, Grep, Bash, TodoWrite, Task
description: Complete Claude Code project setup with MCP tools and configuration
---


## Claude Code Directory Structure

Understanding the Claude Code file organization:

```md
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
