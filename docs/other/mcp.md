# ALL MCP

## integrate to claude code

add `.mcp.json` to your project root

```json
{
  "mcpServers": {
    "context7": {
      "command": "npx",
      "args": [
        "-y",
        "@upstash/context7-mcp@latest"
      ]
    },
    "Sequential-thinking": {
      "command": "npx",
      "args": [
        "-y",
        "@modelcontextprotocol/server-sequential-thinking"
      ]
    },
    "broswertools": {
      "command": "npx",
      "args": [
        "-y",
        "@browsermcp/mcp@latest"
      ]
    },
    "Playwright": {
      "command": "npx",
      "args": [
        "-y",
        "@playwright/mcp@latest"
      ]
    }
  }
}
```
