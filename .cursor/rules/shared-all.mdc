---
description: 
globs: 
alwaysApply: true
---

# 📖 强制执行规则

`run_terminal_cmd` 严格按照[环境配置](mdc:.cursor/rules/shared-dev-env.mdc)选择对应的命令行工具

以下规则文件，执行所有操作前，按需使用 `read_file`

| 优先级 | 规则
| - | - |
| 总是读取 | [编程守则](mdc:.cursor/rules/shared-dev-standard.mdc) |
| 总是读取 | [环境配置](mdc:.cursor/rules/shared-dev-env.mdc) |
| 但凡提及单元测试相关 | [测试守则](mdc:.cursor/rules/test.mdc) |
| 但凡提及Git提交相关 | [提交消息规范](mdc:.cursor/rules/shared-git-commit-message-standard.mdc) |

# 🎯 核心原则

- 执行性能与可维护性优先级高于一切
- 提早发现问题并解决往往成本最低
- 静态类型与编译时检查优先于运行时检查

# ⚡ 性能优化准则

- 严格控制内存分配和复制操作
- 关注关键指标：响应时间、内存占用、启动时间
- 优化算法复杂度，合理控制资源开销
