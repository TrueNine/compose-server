---
argument-hint: [your locale language example <en_US> | <zh_CN> ] [ <your project name> ]
allowed-tools: Read, Write, Edit, MultiEdit, Glob, Grep, Bash, TodoWrite, Task
description: Guide users through the first step of specification-driven development to generate structured requirements documentation
---

Assist users in completing the first step of specification-driven development - requirements documentation writing.

# [STEP-1]: Preparation

- Use _List_ to check if `docs/SPEC-1-REQUIREMENTS.locale.md` exists
- If the file exists, use _Write_ to load content and output a brief overview; otherwise use _Write_ to initialize a new specification document skeleton
- Identify the user's preferred language represented by the first parameter `$1`, subsequent free text output will default to this language; if the user specifies otherwise, follow the latest instructions
- After completing the overview, invite the user to relax and imagine the ideal system form, then WAIT for user input to enter [STEP-2]

# [STEP-2]: Patiently listen to user's initial requirements description

- Recognize that the user is in a relaxed, divergent thinking brainstorming state, expressions may be jumping or metaphorical
- Guide from an architect's perspective: use simple language to break the system into modules, scenarios, or processes, and inquire key information segment by segment
- Listen carefully and record key information, clarify ambiguous points politely when necessary
- Only respond when clarification or confirmation is needed, remain quiet at other times to avoid frequent interruptions
- If the user says "stop" or gives an equivalent request, end the conversation immediately
- The user's divergent description needs to be refined first, select content that aligns with [APPENDIX-1] structure and has executable value before using _Write_ to record; avoid copying sentence by sentence
- If no key points that can be written are filtered out, use WAIT to remain silent and continue listening until clear information is obtained
- During the guidance process, repeat inferences to the user based on phased results, wait for confirmation or correction, avoid making complete design at once
- Continue iterating according to the user's pace until the user explicitly indicates "completion"
- After user confirmation, remind them to use `/translate` or other tools to translate the document to English and record the external link in `project/CLAUDE.md` to improve subsequent coding experience


## Locale Usage Conventions

- `$1` is the locale passed in by the slash command (such as `zh_CN`, `en_US`), also represents the user's preferred language
- When communicating with users, default to using `$1` corresponding language; if the user switches languages or specifies special requirements, follow the latest instructions
- When generating specification documents, except for fixed required English titles or keywords, all other free text should use `$1` language
- Follow common expressions and punctuation of `$1` language, making the copy read naturally without translation feel
- When clarifying terminology or demonstrating examples, you can first explain in `$1` language, and supplement English comparison when necessary


# [APPENDIX-1]: Established format for requirements documentation

When outputting requirements documentation, you must strictly follow the following standard Markdown format specifications:

```md
# [PROJECT_NAME:- $2] User Requirements Documentation
```

**Format Description:**
- `[PROJECT_NAME:- $2]`: Placeholder, needs to be replaced with actual project identifier (such as `mediacms`, `carshoping`, etc.)
- Document title must use English, follow PascalCase naming convention
- Document type is fixed as "User Requirements Documentation"

<Examples>
<Example description="User document example 1">
# mediacms User Requirements Documentation
</Example>
<Example description="User document example 2">
# carshoping User Requirements Documentation
</Example>
<Example description="User document example 2">
# idea-mcp-plugin User Requirements Documentation
</Example>
</Examples>

After a blank line, add the project introduction section, format as follows:

```md
## Introduction

This document records the detailed development requirements for developers in developing [project type] projects,...
```

**Writing Guidelines:**
- Use secondary heading `## Introduction`
- Description should start with a sentence equivalent to "This document records the detailed development requirements for developers in developing" in `$1` language
- Briefly explain the project type and main goals
- Length should be controlled within 2-5 sentences

<Examples>
<Example description="MES system project example">
## Introduction

此文档记录了开发者在开发 MES 系统的详细开发需求，旨在实现生产过程的数字化管理与监控。
</Example>
<Example description="E-commerce project example">
## Introduction

此文档记录了开发者在开发电商前后端分离项目的详细开发需求，涵盖商品管理、订单处理和用户系统等核心功能。
</Example>
</Examples>

After a blank line, define target user groups, format as follows:

```md
**Primary Persona:** [User group description]
```

**Writing Specifications:**
- Fixed use of English title `**Primary Persona:**`
- Use `$1` language to describe user groups, and list multiple groups according to common separators of that language (such as Chinese pause marks, English commas)
- Descriptions need to be concise and accurate, maintaining high relevance to the project field
- Avoid subjective evaluations or artistic expressions

<Examples>
<GoodExample description="Manufacturing industry project">
**Primary Persona:** 制造业员工、制造业开发者
</GoodExample>
<GoodExample description="Education class project">
**Primary Persona:** 在校大学生、高校老师、社团建模爱好者
</GoodExample>
<BadExample description="Error: using Chinese title">
**主要客户群体:** 在校大学生、高校老师、社团建模爱好者
</BadExample>
<BadExample description="Error: containing subjective evaluation">
**Primary Persona:** 富有魅力的企业高管、追求卓越的技术专家
</BadExample>
<BadExample description="Error: description too vague">
**Primary Persona:** 各类用户、有需求的人士
</BadExample>
</Examples>

After a blank line, add optional project constraints, format as follows:

```md
**Operational Constraints:**
1. [Specific constraint description]
2. [Specific constraint description]
3. [Specific constraint description]
```

Constraint type reference (can be flexibly adjusted according to actual situation):
- Infrastructure: Hardware configuration, network environment, deployment method, etc.
- Technology stack: Programming languages, framework choices, third-party services, etc.
- Team configuration: Team size, skill structure, external collaboration, etc.
- Compliance requirements: Industry standards, data security, privacy protection, etc.
- Operational support: Availability goals, maintenance costs, scalability, etc.
- Business factors: Budget constraints, time requirements, return on investment, etc.

<Examples>
<GoodExample description="Video project constraints">
**Operational Constraints:**
1. 服务器性能有限，需要轻量化部署并控制带宽占用
2. 默认依赖外部 MySQL 8；视频资源可部署在本地磁盘或 TOS，视成本取舍
3. 访问与播放量较低，但需确保圈内访问流畅与后台易维护
</GoodExample>
<GoodExample description="Financial project constraints">
**Operational Constraints:**
1. 必须符合国家金融数据安全规范，所有交易数据需加密存储
2. 系统可用性要求 99.9%，每年停机时间不超过 8.76 小时
3. 开发团队 3 人，包括 1 名前端、1 名后端、1 名测试
4. 预算限制在 50 万以内，包含一年的运维成本
</GoodExample>
<BadExample description="Description too vague">
**Operational Constraints:**
1. 服务器要好一点
2. 需要快点完成
3. 预算不太够
</BadExample>
<BadExample description="Using unprofessional expression">
**Operational Constraints:**
1. 电脑配置不能太差，不然跑不动
2. 最好用云服务，这样方便些
3. 找几个人随便做做就行
</BadExample>
</Examples>

After a blank line, add optional non-functional priority description, format as follows:

```md
**Non-Functional Priorities:**
1. [Priority description]
2. [Priority description]
3. [Priority description]
```

<Examples>
<GoodExample description="Clear non-functional priorities">
**Non-Functional Priorities:**
1. 默认启用 HTTPS，优先使用云厂商免费证书
2. 视频与封面优先经由 TOS/CDN；若采用本地存储，需提供容量监控与清理策略
3. 当前仅需桌面端体验，移动端可在后续需求出现时迭代
4. 提供容器或脚本化部署以便迁移与快速恢复
5. 实现轻量日志与监控，并规划数据库与关键数据的定期备份
</GoodExample>

<BadExample description="Vague non-functional priorities">
**Non-Functional Priorities:**
1. 系统要安全稳定
2. 速度要快一点
3. 界面要好看
4. 后期要方便维护
5. 部署要简单
</BadExample>

<GoodExample description="Quantifiable non-functional priorities">
**Non-Functional Priorities:**
1. 所有敏感数据必须 AES-256 加密存储，传输使用 TLS 1.3
2. 核心交易接口响应时间 ≤ 500ms，99% 请求需在 200ms 内完成
3. 系统可用性 ≥ 99.9%，月度停机时间 ≤ 43.2 分钟
4. 支持 Chrome/Firefox/Safari 最新两个版本，IE11 最低兼容
5. 代码覆盖率 ≥ 80%，关键业务 100% 有集成测试
</GoodExample>

<BadExample description="Technology selection rather than priorities">
**Non-Functional Priorities:**
1. 使用 React 框架开发前端
2. 后端采用 Spring Boot 框架
3. 数据库使用 MySQL 8.0
4. 缓存使用 Redis
5. 消息队列用 RabbitMQ
</BadExample>
</Examples>

After a blank line, add optional subsequent scope description, format as follows:

```md
**Deferred Scope:**
1. [Feature description]
2. [Feature description]
3. [Feature description]
```

**Writing Guidelines:**
- Use English title `**Deferred Scope:**`
- List features not considered in the current version, but may need to be implemented in the future
- Each feature should be concise and highlight core value
- Avoid duplication with existing requirements
- Ordered list content should be written in `$1` language

<Examples>
<GoodExample description="Video platform subsequent features">
**Deferred Scope:**
1. 人才市场招聘能力，连接创作者与企业
2. 短剧贩售与付费解锁模块，支持内容变现
3. 创作者社区功能，支持作品交流与协作
</GoodExample>

<GoodExample description="E-commerce platform subsequent features">
**Deferred Scope:**
1. 社交分享功能，允许用户分享商品至各平台
2. 会员积分系统，提升用户忠诚度
3. 多语言国际化支持，拓展海外市场
</GoodExample>

<BadExample description="Description too vague">
**Deferred Scope:**
1. 一些其他功能
2. 后续再加的东西
3. 等有钱了再做的
</BadExample>

<BadExample description="Duplicate with current requirements">
**Deferred Scope:**
1. 用户登录注册（已在基础功能中）
2. 商品展示页面（已在核心需求中）
3. 订单管理功能（已在必须实现中）
</BadExample>
</Examples>


Then comes the core requirements list, which is the most important part of the entire document, must strictly follow the following specifications:

## Requirements Format Specifications

### Basic Structure
```md
## Requirements

### Requirement [Number]: [Requirement Name]

**User Story:** As [User Role], I want [Desired Function], so that [Obtained Value].

#### Acceptance Criteria

1. WHEN [Trigger Condition] THEN [Expected Result]
2. WHEN [Trigger Condition] THEN [Expected Result]
3. WHEN [Trigger Condition] THEN [Expected Result]
```

### Writing Specification Requirements

1. **User Story**
   - Must use standard format: `As [Role], I want [Function], so that [Value]`
   - Role should be specific (such as "creator" rather than "user")
   - Value should be clear (answer "why this feature is needed")
   - Use `$1` language to write [Role], [Function], [Value]

2. **Acceptance Criteria**
   - Must use Given-When-Then format
   - Each criterion must be independent and testable
   - Avoid technical implementation details, focus on business behavior
   - Use `$1` language to write [Trigger Condition], [Expected Result]

3. **Requirement Splitting Principles**
   - Each requirement should be independent with clear value
   - Avoid being too large (consider splitting if exceeding 5 acceptance criteria)
   - Avoid being too small (consider merging if fewer than 2 acceptance criteria)

<Examples>
<GoodExample description="Complete user requirement">
### Requirement 3: User Work Management

**User Story:** As 创作者, I want 能够管理我的所有作品, so that 可以随时编辑或删除内容。

#### Acceptance Criteria

1. WHEN 创作者登录并进入个人中心 THEN 系统应展示其所有作品的列表，包含缩略图、标题、发布时间和浏览量
2. WHEN 创作者点击作品编辑按钮 THEN 系统应跳转至编辑页面，保留原有内容并可修改所有信息
3. WHEN 创作者删除作品 THEN 系统应要求二次确认，成功后从列表中移除并提示用户
4. WHEN 作品被其他用户收藏或评论 THEN 创作者在管理页面应能看到相关统计数据
</GoodExample>

<BadExample description="Missing user value">
### Requirement 2: User Login

**User Story:** As 用户, I want 登录系统。

#### Acceptance Criteria

1. 输入用户名密码
2. 点击登录按钮
3. 登录成功
</BadExample>

<GoodExample description="Technology-agnostic acceptance criteria">
### Requirement 5: Content Recommendation

**User Story:** As 观众, I want 系统能推荐我感兴趣的短剧内容, so that 发现更多优质作品。

#### Acceptance Criteria

1. WHEN 观众浏览首页 THEN 系统应基于其观看历史推荐相似类型作品
2. WHEN 观众完成观看一个作品 THEN 系统应推荐相关创作者的其他作品
3. WHEN 观众连续跳过多个推荐 THEN 系统应调整推荐算法，提供更精准的内容
</GoodExample>

<BadExample description="Containing technical implementation">
### Requirement 4: Video Upload

**User Story:** As 创作者, I want 上传视频。

#### Acceptance Criteria

1. 调用后端 API 接口 /api/v1/videos
2. 使用 MySQL 存储视频信息
3. 视频文件存放在 OSS 对象存储
</BadExample>

<GoodExample description="Reasonable requirement splitting">
### Requirement 7: Comment Interaction

**User Story:** As 观众, I want 对喜欢的作品进行评论, so that 与创作者和其他观众交流想法。

#### Acceptance Criteria

1. WHEN 观众在作品详情页输入评论并提交 THEN 系统应发布评论并在评论区实时显示
2. WHEN 创作者收到评论 THEN 系统应通过站内信通知创作者
3. WHEN 评论包含敏感词 THEN 系统应自动拦截并提示用户修改
4. WHEN 观众点击某条评论 THEN 系统应显示该评论的回复和点赞数
</GoodExample>

<BadExample description="Requirement too complex">
### Requirement 1: Complete User System

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

### Requirement Priority Marking (Optional)
If need to identify requirement priorities, use markers after the number:
- `[H]` - High priority
- `[M]` - Medium priority
- `[L]` - Low priority

<Examples>
<Example description="Priority marking example">
### Requirement 1[H]: User Authentication
### Requirement 2[M]: Email Notification
### Requirement 3[L]: Theme Switching
</Example>
</Examples>

<Example description="Complete example: Online education platform requirements document">
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

### Requirement 1[H]: Course Creation and Management

**User Story:** As 教师, I want 能够创建和管理在线课程, so that 可以灵活地安排教学内容和进度。

#### Acceptance Criteria

1. WHEN 教师登录并进入课程管理页面 THEN 系统应显示"创建新课程"按钮和现有课程列表
2. WHEN 教师点击"创建新课程"并填写课程信息 THEN 系统应生成课程主页并支持添加章节
3. WHEN 教师上传视频课件 THEN 系统应自动转码为多格式以适应不同网络环境
4. WHEN 教师设置课程价格 THEN 系统应支持免费、付费和会员专享三种模式
5. WHEN 课程有学员报名 THEN 教师应收到通知并查看学员名单

### Requirement 2[H]: Video Live Teaching

**User Story:** As 教师, I want 进行实时视频直播教学, so that 可以与学生进行互动和答疑。

#### Acceptance Criteria

1. WHEN 教师进入直播间 THEN 系统应提供摄像头、麦克风和屏幕共享选项
2. WHEN 教师开始直播 THEN 系统应自动通知已报名的学生
3. WHEN 学生在直播中提问 THEN 教师应能看到实时弹幕并选择性回复
4. WHEN 网络不稳定时 THEN 系统应自动切换至清晰度较低的流畅模式
5. WHEN 直播结束后 THEN 系统应生成回放视频并自动关联到课程页面

### Requirement 3[M]: Learning Progress Tracking

**User Story:** As 学生, I want 能够查看我的学习进度, so that 了解完成情况并制定学习计划。

#### Acceptance Criteria

1. WHEN 学生进入个人中心 THEN 系统应展示已购课程列表和整体学习进度
2. WHEN 学生进入课程详情页 THEN 系统应显示每个章节的完成状态和学习时长
3. WHEN 学生完成一个章节 THEN 系统应自动更新进度并解锁下一章节
4. WHEN学生的学习时长达到系统设定值 THEN 系统应弹出休息提醒
5. WHEN 学生完成全部课程 THEN 系统应生成电子证书并支持分享

### Requirement 4[M]: Interactive Discussion Area

**User Story:** As 学生, I want 能够在课程下进行讨论和提问, so that 与同学和老师交流学习心得。

#### Acceptance Criteria

1. WHEN 学生进入课程讨论区 THEN 系统应按时间顺序显示所有讨论帖
2. WHEN 学生发布提问 THEN 系统应@通知相关教师和其他选课学生
3. WHEN 教师回复问题 THEN 系统应标记为"已解答"并高亮显示
4. WHEN 学生觉得某个回答有用 THEN 可以点赞该回答
5. WHEN 讨论包含不当内容 THEN 系统应自动过滤并提交人工审核

### Requirement 5[L]: Assignment Submission and Grading

**User Story:** As 学生, I want 在线提交作业并获得老师反馈, so that 及时了解自己的学习效果。

#### Acceptance Criteria

1. WHEN 教师发布作业 THEN 系统应通知所有选课学生并显示截止日期
2. WHEN 学生提交作业 THEN 系统应支持文本、图片、文档和视频多种格式
3. WHEN 学生提交后超时 THEN 系统应自动关闭提交入口
4. WHEN 教师批改作业 THEN 系统应支持评分、评语和批注功能
5. WHEN 所有作业批改完成后 THEN 系统应生成班级成绩统计
</Example>


### Q & A

**Q: How detailed should requirements be?**
A: Each requirement should be detailed enough for developers to understand and implement, but avoid over-designing. Generally 3-5 acceptance criteria are appropriate.

**Q: How to write acceptance criteria to ensure testability?**
A: Use specific, observable results, avoid vague words like "fast", "friendly", etc., change to specific indicators like "response time < 2 seconds".

**Q: How to judge if requirement splitting is reasonable?**
A: If a requirement has more than 5 acceptance criteria, consider whether it can be split; if fewer than 2, consider whether it is too simple.