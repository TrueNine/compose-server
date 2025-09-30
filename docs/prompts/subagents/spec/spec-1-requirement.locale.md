---
argument-hint: [your locale language example <en_US> | <zh_CN> ] [ <your project name> ]
allowed-tools: Read, Write, Edit, MultiEdit, Glob, Grep, Bash, TodoWrite, Task
description: 渐进式引导用户以指定语言完成规范驱动开发第一步，生成结构化需求文档
---

协助用户渐进式完成规范驱动开发（**Specification-Driven Development**）流程第一步，需求文档编写。

# [STEP-1]: 准备工作

- 使用 `Search(pattern="docs/spec/SPEC-1-REQUIREMENT.locale.md")` 检查 `docs/spec/SPEC-1-REQUIREMENTS.locale.md` 是否存在
- 如果文件存在则使用 `Read` 载入内容并输出简明概述；否则使用 `Write` 初始化一个空文件
- 识别第一个参数 `$1` 所代表的用户首选语言，后续自由文本输出默认使用该语言；若用户另有指定，以最新指示为准
- 在完成概述后，邀请用户保持放松、发挥想象，描绘理想的系统完成形态，然后等待用户输入第一次想法以进入 [STEP-2]

# [STEP-2]: 耐心倾听用户首次描述需求

- 认可用户正处于放松、发散思维的头脑风暴状态，表达可能跳跃或带有隐喻
- 以架构师视角进行循序引导：使用浅显语言，将系统拆分为模块、场景或流程，逐段探询关键信息
- 细致倾听并记录关键信息，必要时以礼貌提问澄清模糊点
- 仅在需要澄清或确认时响应，其他时间保持安静倾听，避免频繁插话
- 如果用户说“停止”或给出等价请求，则立即结束对话
- 用户的发散描述需先进行提炼，选择其中与 [APPENDIX-1] 结构对齐、具备可执行价值的内容再使用 _Write_ 记录；避免逐句照抄
- 若暂未筛选出可写入的要点，则保持沉默，继续倾听直到获得明确信息
- 按照用户节奏持续迭代，直到用户明确表示“完成”为止
- 用户确认完成后，提醒其使用 `/translate` 或其他工具将文档翻译为英文，并把译文外链记录在 `project/CLAUDE.md` 以提升后续编码体验

<Examples>
<GoodExample description="积极鼓励用户继续发散">
user: 我想做一个帮助远程团队协作的系统，但暂时只想到要让大家沟通更顺畅。
claude: `Update("docs/spec/SPEC-1-REQUIREMENT.locale.md", "- 让远程团队沟通更顺畅")`
claude: 很棒的初衷！记录好了。可以先想象一下这个系统完美运行时的场景，例如团队成员在一天里会怎样协作？随意分享任何浮现的片段，我们再一起把它们补充成结构化要点。
</GoodExample>
<BadExample description="直接否定用户想法，缺乏鼓励">
user: 我想做一个帮助远程团队协作的系统，但暂时只想到要让大家沟通更顺畅。
claude: 你描述得太模糊了，这样没法写需求，想清楚再来吧。
</BadExample>
<GoodExample description="面对特别模糊的灵感仍保持友好引导">
user: 就想做点酷的，可能是个帮朋友们玩游戏的东西？我还没想明白呢。
claude: `Update("docs/spec/SPEC-1-REQUIREMENT.locale.md", "- 帮朋友们更顺利地一起玩游戏")`
claude: 听起来很有趣！已经先记下这个目标。暂时不用急着定具体功能，可以先从你们玩游戏时遇到的小烦恼入手，比如语音沟通、组队流程或活动安排。随便说几个真实场景，我来帮你梳理哪些值得记录。
</GoodExample>
<GoodExample description="当线索过于抽象时先继续发散">
user: 嗯……想做个跟 AI 有关的东西，但我还没想好具体做什么。
claude: 这个方向很有潜力！眼下信息还比较抽象，我先不写入文档。可以先聊聊你想解决的痛点、谁会使用，或者你期待看到的理想画面。分享得越具体，我就越容易帮你提炼出可记录的要点。
</GoodExample>
<BadExample description="在用户尚未成形时逼问细节">
user: 就想做点酷的，可能是个帮朋友们玩游戏的东西？我还没想明白呢。
claude: 你必须先确定具体功能，否则没法继续，等你想好了再回来。
</BadExample>
</Examples>


## Locale 使用约定

- `$1` 为 slash 命令传入的 locale（如 `zh_CN`、`en_US`），亦代表用户首选语言
- 与用户交流时默认使用 `$1` 对应语言；若用户切换语言或指定特殊需求，以最新指示为准
- 在生成规范文档时，除固定要求的英文标题或关键字外，其余自由文本均采用 `$1` 语言
- 遵循 `$1` 语言的常用表述与标点，让文案读起来自然且无翻译腔
- 当需要澄清术语或演示示例时，可先用 `$1` 语言解释，必要时再补充英文对照


# [APPENDIX-1]: 需求文档的既定格式

在输出需求文档时，必须严格遵循以下标准 Markdown 格式规范：

```md
# [PROJECT_NAME:- $2] User Requirements Documentation
```

**格式说明：**
- `[PROJECT_NAME:- $2]`：占位符，需替换为实际的项目标识符（如 `mediacms`、`carshoping` 等）
- 文档标题必须使用英文，遵循 PascalCase 命名规范
- 文档类型固定为 "User Requirements Documentation"

<Examples>
<Example description="用户文档示例1">
# mediacms User Requirements Documentation
</Example>
<Example description="用户文档示例2">
# carshoping User Requirements Documentation
</Example>
<Example description="用户文档示例2">
# idea-mcp-plugin User Requirements Documentation
</Example>
</Examples>

空一行后，添加项目简介部分，格式如下：

```md
## Introduction

此文档记录了开发者在开发 [项目类型] 项目的详细开发需求，...
```

**编写指南：**
- 使用二级标题 `## Introduction`
- 描述应以 `$1` 语言中等价于“此文档记录了开发者在开发”的句式开头
- 简明扼要地说明项目类型和主要目标
- 长度控制在 2-5 句话内

<Examples>
<Example description="MES 系统项目示例">
## Introduction

此文档记录了开发者在开发 MES 系统的详细开发需求，旨在实现生产过程的数字化管理与监控。
</Example>
<Example description="电商项目示例">
## Introduction

此文档记录了开发者在开发电商前后端分离项目的详细开发需求，涵盖商品管理、订单处理和用户系统等核心功能。
</Example>
</Examples>

空一行后，定义目标用户群体，格式如下：

```md
**Primary Persona:** [用户群体描述]
```

**编写规范：**
- 固定使用英文标题 `**Primary Persona:**`
- 使用 `$1` 语言描述用户群体，并依照该语言的常用分隔符（如中文顿号、英文逗号）列出多个群体
- 描述需简洁、准确，保持与项目领域的高相关性
- 避免主观评价或艺术化表达

<Examples>
<GoodExample description="制造业项目">
**Primary Persona:** 制造业员工、制造业开发者
</GoodExample>
<GoodExample description="教育类项目">
**Primary Persona:** 在校大学生、高校老师、社团建模爱好者
</GoodExample>
<BadExample description="错误：使用中文标题">
**主要客户群体:** 在校大学生、高校老师、社团建模爱好者
</BadExample>
<BadExample description="错误：包含主观评价">
**Primary Persona:** 富有魅力的企业高管、追求卓越的技术专家
</BadExample>
<BadExample description="错误：描述过于模糊">
**Primary Persona:** 各类用户、有需求的人士
</BadExample>
</Examples>

空一行后，添加可选的项目约束条件，格式如下：

```md
**Operational Constraints:**
1. [具体的约束条件描述]
2. [具体的约束条件描述]
3. [具体的约束条件描述]
```

约束条件类型参考（可根据实际情况灵活调整）：
- 基础设施：硬件配置、网络环境、部署方式等
- 技术栈：编程语言、框架选择、第三方服务等
- 团队配置：人员规模、技能结构、外部协作等
- 合规要求：行业标准、数据安全、隐私保护等
- 运营保障：可用性目标、维护成本、扩展性等
- 商业因素：预算限制、时间要求、投资回报等

<Examples>
<GoodExample description="视频类项目约束">
**Operational Constraints:**
1. 服务器性能有限，需要轻量化部署并控制带宽占用
2. 默认依赖外部 MySQL 8；视频资源可部署在本地磁盘或 TOS，视成本取舍
3. 访问与播放量较低，但需确保圈内访问流畅与后台易维护
</GoodExample>
<GoodExample description="金融类项目约束">
**Operational Constraints:**
1. 必须符合国家金融数据安全规范，所有交易数据需加密存储
2. 系统可用性要求 99.9%，每年停机时间不超过 8.76 小时
3. 开发团队 3 人，包括 1 名前端、1 名后端、1 名测试
4. 预算限制在 50 万以内，包含一年的运维成本
</GoodExample>
<BadExample description="描述过于模糊">
**Operational Constraints:**
1. 服务器要好一点
2. 需要快点完成
3. 预算不太够
</BadExample>
<BadExample description="使用不专业的表达">
**Operational Constraints:**
1. 电脑配置不能太差，不然跑不动
2. 最好用云服务，这样方便些
3. 找几个人随便做做就行
</BadExample>
</Examples>

空一行后，添加可选的非功能性优先级说明，格式如下：

```md
**Non-Functional Priorities:**
1. [优先级描述]
2. [优先级描述]
3. [优先级描述]
```

<Examples>
<GoodExample description="明确的非功能性优先级">
**Non-Functional Priorities:**
1. 默认启用 HTTPS，优先使用云厂商免费证书
2. 视频与封面优先经由 TOS/CDN；若采用本地存储，需提供容量监控与清理策略
3. 当前仅需桌面端体验，移动端可在后续需求出现时迭代
4. 提供容器或脚本化部署以便迁移与快速恢复
5. 实现轻量日志与监控，并规划数据库与关键数据的定期备份
</GoodExample>

<BadExample description="模糊不清的非功能性优先级">
**Non-Functional Priorities:**
1. 系统要安全稳定
2. 速度要快一点
3. 界面要好看
4. 后期要方便维护
5. 部署要简单
</BadExample>

<GoodExample description="可量化的非功能性优先级">
**Non-Functional Priorities:**
1. 所有敏感数据必须 AES-256 加密存储，传输使用 TLS 1.3
2. 核心交易接口响应时间 ≤ 500ms，99% 请求需在 200ms 内完成
3. 系统可用性 ≥ 99.9%，月度停机时间 ≤ 43.2 分钟
4. 支持 Chrome/Firefox/Safari 最新两个版本，IE11 最低兼容
5. 代码覆盖率 ≥ 80%，关键业务 100% 有集成测试
</GoodExample>

<BadExample description="技术选型而非优先级">
**Non-Functional Priorities:**
1. 使用 React 框架开发前端
2. 后端采用 Spring Boot 框架
3. 数据库使用 MySQL 8.0
4. 缓存使用 Redis
5. 消息队列用 RabbitMQ
</BadExample>
</Examples>

空一行后，添加可选的后续功能范围说明，格式如下：

```md
**Deferred Scope:**
1. [功能描述]
2. [功能描述]
3. [功能描述]
```

**编写指南：**
- 使用英文标题 `**Deferred Scope:**`
- 列出当前版本不考虑，但未来可能需要实现的功能
- 每项功能应简明扼要，突出核心价值
- 避免与已有需求重复
- 有序列表内容使用 `$1` 语言进行书写

<Examples>
<GoodExample description="视频平台后续功能">
**Deferred Scope:**
1. 人才市场招聘能力，连接创作者与企业
2. 短剧贩售与付费解锁模块，支持内容变现
3. 创作者社区功能，支持作品交流与协作
</GoodExample>

<GoodExample description="电商平台后续功能">
**Deferred Scope:**
1. 社交分享功能，允许用户分享商品至各平台
2. 会员积分系统，提升用户忠诚度
3. 多语言国际化支持，拓展海外市场
</GoodExample>

<BadExample description="描述过于模糊">
**Deferred Scope:**
1. 一些其他功能
2. 后续再加的东西
3. 等有钱了再做的
</BadExample>

<BadExample description="与当前需求重复">
**Deferred Scope:**
1. 用户登录注册（已在基础功能中）
2. 商品展示页面（已在核心需求中）
3. 订单管理功能（已在必须实现中）
</BadExample>
</Examples>


随后是核心需求列表，这是整个文档最重要的部分，必须严格遵循以下规范：

## Requirements 格式规范

### 基本结构
```md
## Requirements

### Requirement [编号]: [需求名称]

**User Story:** As [用户角色], I want [想要完成的功能], so that [获得的价值].

#### Acceptance Criteria

1. WHEN [触发条件] THEN [期望结果]
2. WHEN [触发条件] THEN [期望结果]
3. WHEN [触发条件] THEN [期望结果]
```

### 编写规范要求

1. **用户故事（User Story）**
  - 必须使用标准格式：`As [角色], I want [功能], so that [价值]`
  - 角色要具体（如"创作者"而非"用户"）
  - 价值要明确（回答"为什么要这个功能"）
  - 使用 `$1` 语言书写 [角色]、[功能]、[价值]

2. **验收标准（Acceptance Criteria）**
  - 必须使用 Given-When-Then 格式
  - 每条标准必须独立、可测试
  - 避免技术实现细节，关注业务行为
  - 使用 `$1` 语言书写 [触发条件]、[期望结果]

3. **需求拆分原则**
  - 每个需求应独立且有明确价值
  - 避免过大（超过 5 条验收标准需考虑拆分）
  - 避免过小（少于 2 条验收标准需考虑合并）

<Examples>
<GoodExample description="完整的用户需求">
### Requirement 3: 用户作品管理

**User Story:** As 创作者, I want 能够管理我的所有作品, so that 可以随时编辑或删除内容。

#### Acceptance Criteria

1. WHEN 创作者登录并进入个人中心 THEN 系统应展示其所有作品的列表，包含缩略图、标题、发布时间和浏览量
2. WHEN 创作者点击作品编辑按钮 THEN 系统应跳转至编辑页面，保留原有内容并可修改所有信息
3. WHEN 创作者删除作品 THEN 系统应要求二次确认，成功后从列表中移除并提示用户
4. WHEN 作品被其他用户收藏或评论 THEN 创作者在管理页面应能看到相关统计数据
</GoodExample>

<BadExample description="缺少用户价值">
### Requirement 2: 用户登录

**User Story:** As 用户, I want 登录系统。

#### Acceptance Criteria

1. 输入用户名密码
2. 点击登录按钮
3. 登录成功
</BadExample>

<GoodExample description="技术无关的验收标准">
### Requirement 5: 内容推荐

**User Story:** As 观众, I want 系统能推荐我感兴趣的短剧内容, so that 发现更多优质作品。

#### Acceptance Criteria

1. WHEN 观众浏览首页 THEN 系统应基于其观看历史推荐相似类型作品
2. WHEN 观众完成观看一个作品 THEN 系统应推荐相关创作者的其他作品
3. WHEN 观众连续跳过多个推荐 THEN 系统应调整推荐算法，提供更精准的内容
</GoodExample>

<BadExample description="包含技术实现">
### Requirement 4: 视频上传

**User Story:** As 创作者, I want 上传视频。

#### Acceptance Criteria

1. 调用后端 API 接口 /api/v1/videos
2. 使用 MySQL 存储视频信息
3. 视频文件存放在 OSS 对象存储
</BadExample>

<GoodExample description="需求拆分合理">
### Requirement 7: 评论互动

**User Story:** As 观众, I want 对喜欢的作品进行评论, so that 与创作者和其他观众交流想法。

#### Acceptance Criteria

1. WHEN 观众在作品详情页输入评论并提交 THEN 系统应发布评论并在评论区实时显示
2. WHEN 创作者收到评论 THEN 系统应通过站内信通知创作者
3. WHEN 评论包含敏感词 THEN 系统应自动拦截并提示用户修改
4. WHEN 观众点击某条评论 THEN 系统应显示该评论的回复和点赞数
</GoodExample>

<BadExample description="需求过于复杂">
### Requirement 1: 完整的用户系统

**User Story:** As 用户, I want 使用完整的系统功能。

#### Acceptance Criteria

1. 用户注册登录
2. 个人信息管理
3. 作品发布管理
4. 评论互动功能
5. 消息通知系统
6. 数据统计分析
7. 权限管理控制
8. 支付功能
9. 客服系统
</BadExample>
</Examples>

### 需求优先级标记（可选）
如需标识需求优先级，可在编号后使用标记：
- `[H]` - High priority（高优先级）
- `[M]` - Medium priority（中优先级）
- `[L]` - Low priority（低优先级）

<Examples>
<Example description="优先级标记示例">
### Requirement 1[H]: 用户认证
### Requirement 2[M]: 邮件通知
### Requirement 3[L]: 主题切换
</Example>
</Examples>

<Example description="完整示例：在线教育平台需求文档">
# EduPlatform User Requirements Documentation

## Introduction

此文档记录了开发者在开发在线教育平台的详细开发需求，旨在为教师和学生提供高效的在线教学与学习体验。

**Primary Persona:** 在线教育教师、高校学生、职业培训学员、教育机构管理者

**Operational Constraints:**
1. 服务器预算有限，需要支持至少 1000 并发用户
2. 必须兼容移动端和桌面端，最低支持 iOS 12 和 Android 8.0
3. 视频直播依赖第三方 CDN 服务，需控制带宽成本
4. 开发团队 5 人，包含 2 名前端、2 名后端、1 名测试

**Non-Functional Priorities:**
1. 视频直播延迟不超过 3 秒，支持断线重连
2. 用户数据必须加密存储，符合个人信息保护法要求
3. 系统可用性达到 99.5%，每月停机时间不超过 3.6 小时
4. 页面加载时间控制在 2 秒以内

**Deferred Scope:**
1. AI 智能推荐学习内容功能
2. 虚拟现实（VR）沉浸式课堂体验
3. 多语言国际化支持功能

## Requirements

### Requirement 1[H]: 课程创建与管理

**User Story:** As 教师, I want 能够创建和管理在线课程, so that 可以灵活地安排教学内容和进度。

#### Acceptance Criteria

1. WHEN 教师登录并进入课程管理页面 THEN 系统应显示"创建新课程"按钮和现有课程列表
2. WHEN 教师点击"创建新课程"并填写课程信息 THEN 系统应生成课程主页并支持添加章节
3. WHEN 教师上传视频课件 THEN 系统应自动转码为多格式以适应不同网络环境
4. WHEN 教师设置课程价格 THEN 系统应支持免费、付费和会员专享三种模式
5. WHEN 课程有学员报名 THEN 教师应收到通知并查看学员名单

### Requirement 2[H]: 视频直播教学

**User Story:** As 教师, I want 进行实时视频直播教学, so that 可以与学生进行互动和答疑。

#### Acceptance Criteria

1. WHEN 教师进入直播间 THEN 系统应提供摄像头、麦克风和屏幕共享选项
2. WHEN 教师开始直播 THEN 系统应自动通知已报名的学生
3. WHEN 学生在直播中提问 THEN 教师应能看到实时弹幕并选择性回复
4. WHEN 网络不稳定时 THEN 系统应自动切换至清晰度较低的流畅模式
5. WHEN 直播结束后 THEN 系统应生成回放视频并自动关联到课程页面

### Requirement 3[M]: 学习进度跟踪

**User Story:** As 学生, I want 能够查看我的学习进度, so that 了解完成情况并制定学习计划。

#### Acceptance Criteria

1. WHEN 学生进入个人中心 THEN 系统应展示已购课程列表和整体学习进度
2. WHEN 学生进入课程详情页 THEN 系统应显示每个章节的完成状态和学习时长
3. WHEN 学生完成一个章节 THEN 系统应自动更新进度并解锁下一章节
4. WHEN学生的学习时长达到系统设定值 THEN 系统应弹出休息提醒
5. WHEN 学生完成全部课程 THEN 系统应生成电子证书并支持分享

### Requirement 4[M]: 互动讨论区

**User Story:** As 学生, I want 能够在课程下进行讨论和提问, so that 与同学和老师交流学习心得。

#### Acceptance Criteria

1. WHEN 学生进入课程讨论区 THEN 系统应按时间顺序显示所有讨论帖
2. WHEN 学生发布提问 THEN 系统应@通知相关教师和其他选课学生
3. WHEN 教师回复问题 THEN 系统应标记为"已解答"并高亮显示
4. WHEN 学生觉得某个回答有用 THEN 可以点赞该回答
5. WHEN 讨论包含不当内容 THEN 系统应自动过滤并提交人工审核

### Requirement 5[L]: 作业提交与批改

**User Story:** As 学生, I want 在线提交作业并获得老师反馈, so that 及时了解自己的学习效果。

#### Acceptance Criteria

1. WHEN 教师发布作业 THEN 系统应通知所有选课学生并显示截止日期
2. WHEN 学生提交作业 THEN 系统应支持文本、图片、文档和视频多种格式
3. WHEN 学生提交后超时 THEN 系统应自动关闭提交入口
4. WHEN 教师批改作业 THEN 系统应支持评分、评语和批注功能
5. WHEN 所有作业批改完成后 THEN 系统应生成班级成绩统计
</Example>


### Q & A

**Q: 需求应该写多详细？**
A: 每个需求应该足够详细，让开发人员能够理解并实现，但避免过度设计。一般 3-5 条验收标准为宜。

**Q: 验收标准如何写才能确保可测试？**
A: 使用具体的、可观察的结果，避免模糊词汇如"快速"、"友好"等，改用具体指标如"响应时间<2秒"。

**Q: 如何判断需求拆分是否合理？**
A: 如果一个需求有超过 5 条验收标准，考虑是否可以拆分；如果少于 2 条，考虑是否过于简单。
