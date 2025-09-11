# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this Repository.

# Tool Use

## Documentation Tools

1. **查看官方文档**
  - `resolve-library-id` - 解析库名到 Context7 ID
  - `get-library-docs` - 获取最新官方文档

2. **搜索真实代码**
  - `searchGitHub` - 搜索 GitHub 上的实际使用案例

## 编写规范文档工具

编写需求和设计文档时使用 `specs-workflow`：

1. **检查进度**: `action.type="check"`
2. **初始化**: `action.type="init"`
3. **更新任务**: `action.type="complete_task"`

路径：`/docs/specs/*`
