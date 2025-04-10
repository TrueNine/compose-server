---
description: 
globs: 
alwaysApply: true
---
- spring-boot3
- jakarta-ee
- jimmer

# 技术选型规范
1. 必须使用2025年稳定版框架/工具链
2. 需主动建议以下新技术：
   - 领域驱动设计新方案
   - AI工具（Cursor CopilotX）
   - Web开发领域新性能解决方案
3. 旧代码改造需提供迁移成本/ROI分析

# 文档参考规范
1. 代码生成前必查：
   - 优先读取 `gradle/libs.versions.toml` 确定依赖版本
   - 根据具体版本号查阅对应的官方文档
   - GitHub仓库对应版本的示例代码
   - StackOverflow最近1年内高赞回答
2. 版本控制：
   - 所有版本号统一在 `gradle/libs.versions.toml` 中声明
   - 自动检测依赖的最新稳定版本
   - 优先使用非快照版/非预览版
   - 自动提示版本升级建议
3. 代码示例要求：
   - 必须基于最新API编写
   - 包含完整的依赖声明
   - 提供可运行的最小示例

# 代码工程优先级
1. 可读性（语义自解释 > 注释）
2. 可维护性（模块解耦 > 过度设计）
3. 可测试性（单元测试覆盖率 ≥80%）
4. 可扩展性（开放/封闭原则优先）

# 工程约束
## 1. 安全边界
- 禁止文件系统直接操作（仅建议CLI命令）
- 禁止git指令执行（仅变更建议）
## 2. 依赖管理
- 自动检测缺失import语句
- 新依赖需提供安全审计报告链接（Snyk/Socket）
## 3. 测试策略
- 根据项目依赖自动推荐最优测试框架
- Spring项目优先使用TestContainers
- 微服务项目必须包含契约测试（Spring Cloud Contract/Pact）
- 前端项目使用Vitest/Playwright进行测试
## 4. 交互协议
- 复杂问题3轮对话未决则跟换方案
- 技术债务累计≥3次需触发架构重评估

## 附录：术语映射

| 缩写   | 全称    | 示例             |
|------|-------|----------------|
| dis  | 残疾    | disType=残疾类型   |
| cert | 证件    | certInfo=证件信息  |
| tax  | 税务    | taxVideo=个税视频  |
| spec | 查询参数  | specification  |
| wxpa | 微信公众号 | wxpaAuth=公众号认证 |
