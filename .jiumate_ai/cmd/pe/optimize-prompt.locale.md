---
argument-hint: [locale_file_at_path]
allowed-tools: Read, Write, Edit, Glob, Grep, Bash
description: 优化 记忆提示词文件, 使得其记忆提示词文件让 "AI Agent" 更具可读性, 达到更好的工作效果
---



`optimize-prompt` 负责梳理用户意图并优化 `AI Agent` `$1` 记忆提示词的描述, 同时依据可选参数 `$2` 执行定制化调整.
流程遵循既定规则对记忆提示词进行结构优化, 格式规范与内容精简, 如果用户没有提供 `$1`, 任务会立即退出.
请注意:待优化的记忆提示词通常存在格式错误, 冗余描述或未完成的草稿信息, 修改时务必保持可追溯性.




## 优化规则



### 语言选择规则

- **优先处理 `**.locale.*` 文件**: 当文件名包含 `**.locale.*` 时, 直接执行流程
- **翻译非 .locale. 文件**: 如果用户传入的不是 `**.locale.*` 文件, 则在其旁边翻译一份中文的 `**.locale.*` 文件, 并对翻译后的 `**.locale.*` 文件进行优化
- **始终保持用户对提示词的感知能力**: 通过自动翻译和优化机制, 确保用户始终能够理解和控制提示词内容
- 面对 `**.locale.md` 文件时, 使用美式英语逻辑撰写英式中文, 术语保持英文原文



### 文档开头的简要描述

- 记忆提示词文件第一段需要用 2-5 句概述全文目的
- 保持表述简明直白, 不进行修辞堆砌



### 标题结构优化

- 标题嵌套级别不得大于3级
- `#` 仅用于一级标题, `##` 仅用于二级标题
- 去除冗杂的标题嵌套
- 确保标题层次清晰



### 内容表达规范

- **禁止使用表情符号**: 文档中严格禁止使用任何表情符号(emoji)保持专业性
- 使用简洁明了的书面语表达
- 保持文档风格一致性和专业性
- 摈弃华丽词藻与刻意的中文对齐排版, 直接呈现关键信息



### 术语转换规则

- 将“大驼峰命名”统一写作 `PascalCase`
- 将“小驼峰命名”统一写作 `camelCase`
- 将“蛇形命名”统一写作 `snake_case`
- 将“烤串命名”统一写作 `kebab-case`



### 示例编写规范

- **XML 容器要求**
  - 使用结构化 `XML` 标签包裹示例, 方便解析与复用
  - 将示例置于 ` ```xml ... ``` ` 代码块中, 统一展示格式
  - 标签属性一律使用英文引号, 避免混用
  - 标签名称必须使用 `PascalCase`, 例如 `<Example>`

- **可用标签**
  - `<Examples>`: 顶层容器, 用于组合同一主题的示例
  - `<Example>`: 通用示例, 展示标准做法
  - `<GoodExample>`: 正向示例, 仅能出现在 `<Examples>` 内
  - `<BadExample>`: 反向示例, 仅能出现在 `<Examples>` 内
  - `<Thinking>`: 描述思考过程, 只能内嵌于单个示例
  - `<Tooling>`: 记录示例涉及的工具命令, 必须添加 `name="..."`

- **属性约定**
  - `description="..."`: 可选, 简要说明示例意图
  - `userInput="..."`: 可选, 展示示例对应的用户输入
  - `params:path="..."`: 可选, 记录示例相关的文件路径
  - `params:command="..."`: 可选, 描述执行命令或脚本
  - 其他 `params:*` 属性沿用 `params:<key>="<value>"` 语法, 必须使用英文属性名
  - 可选属性无需为了凑格式而添加, 未涉及的信息保持缺省
  - `description` 内容直接说明场景或风险点, 不重复描述“正确示例”“错误示例”等标签语义

- **原子化原则**
  - 每个示例只覆盖一个概念, 不拆分多个主题
  - 示例中禁止使用行内注释, 说明信息需放在结构化内容中
  - `<Tooling>` 节点需包含 `name="<tool-name>"`, 并根据需要补充 `params:*`
  - 仅保留一个连续的代码段或输出片段
  - 示例内容必须自洽, 无需额外上下文即可理解


```xml
<Examples>
  <GoodExample>
    fn process_data(data: &str) -> Result<ProcessedData, Error> {
      parse_data(data)
    }
  </GoodExample>

  <BadExample description="缺少显式错误处理">
    fn process_data(data: &str) -> Result<ProcessedData, Error> {
      parse_data(data)
    }
  </BadExample>

  <BadExample description="同一示例混入额外概念">
    fn get_optional_value() -> Option<String> {
      Some("value".to_string())
    }
  </BadExample>
</Examples>
```



### 核心结构要素

- **角色定义**: 明确AI的身份和专业背景
- **任务描述**: 清晰具体地说明要完成的任务
- **约束条件**: 明确限制和要求
- **输出格式**: 指定输出的结构和格式



### 注意力机制优化

- **核心要点限制**: 每个提示词最多突出3个核心要点
- **避免注意力稀释**: 过度使用强调格式(粗体, 代码块等)会降低效果
- **位置策略**: 将最关键信息放在开头和结尾



### 提示词长度优化

- **精简原则**: 去除冗余描述, 保留核心信息
- **必要细节**: 保留关键的技术细节和约束条件
- **可读性**: 合理分段, 避免过长的段落



### 提示词文件结构要求

- **YAML 前置配置**: 文件开头可能包含 YAML 配置块, 定义工具权限和基本描述
- **描述性文本**: 除了 YAML 配置外, 还应包含一段文字描述, 说明记忆提示词的用途和功能, 大约 2-5 句
- **结构完整**: 确保提示词文件既有配置信息, 又有功能说明
- **占位清理**: 移除历史遗留的空标题或无效标记, 保持结构紧凑



### 格式优化技巧

- **编码规范**: 使用 UTF-8 编码确保兼容性
- **缩进规范**: 统一使用 2 空格缩进
- **行结束符**: 使用 LF 行结束符(非 CRLF)
- **格式一致性**: 确保整个文档格式风格统一
- **标点符号规范**: 禁止使用中文标点符号, 统一使用英文标点符号
- 禁止在正文使用 `---` 水平线, YAML 配置块除外



### 文件结构表示规范

- **禁止使用树形结构图**: 不使用 ASCII 艺术风格的树形图来表示文件结构
- **使用缩进方式**: 文件结构必须使用简单的缩进格式表示
- **清晰简洁**: 确保结构清晰易读, 避免过度复杂的表示方法

```xml
<Examples>
  <GoodExample>
    .docs/
    - `prompts/` - 提示词模板
    - `user/` - 全局用户提示词
    - `project/` - 项目级提示词
    - `slashcommands/` - 斜杠命令提示词
    - `qa/` - 问答文档
    - `references/` - 技术参考文档
    - `other/` - 其他文档(构建, Git, 数据库等)
  </GoodExample>

  <BadExample description="使用树形结构图">
    .docs/
    ├── prompts/ # 提示词模板
    │ ├── user/ # 全局用户提示词
    │ ├── project/ # 项目级提示词
    │ └── slashcommands/ # 斜杠命令提示词
    ├── qa/ # 问答文档
    ├── references/ # 技术参考文档
    └── other/ # 其他文档(构建, Git, 数据库等)
  </BadExample>
</Examples>
```



### 明确性优化

- **避免歧义**: 使用精确的词汇, 避免模糊表达
- **具体化**: 将抽象概念转化为具体要求
- **可执行性**: 确保指令可以被准确理解和执行



### 约束条件明确化

- **必须包含**: 明确列出必须满足的条件
- **禁止事项**: 清晰说明不能做什么
- **边界条件**: 定义处理的范围和限制



### 输出标准化

- **格式规范**: 指定具体的输出格式(表格, 列表, 代码块等)
- **结构要求**: 明确输出的组织结构
- **示例说明**: 提供期望输出的示例



### 标点符号使用示例

```xml
<Examples>
  <GoodExample description="使用英文标点符号">
    # Role: Code Review Assistant

    You are an expert code reviewer with 10+ years of experience. Your task is to:
    1. Analyze code quality and identify potential issues
    2. Provide actionable feedback for improvements
    3. Ensure code follows best practices and security guidelines

    Focus on readability, maintainability, and performance aspects.
  </GoodExample>
  <BadExample description="使用中文标点符号">
    # Role: 代码审查助手

    你是一位拥有10年以上经验的专家代码审查员。你的任务是:
    1. 分析代码质量并识别潜在问题
    2. 提供可操作的改进建议
    3. 确保代码遵循最佳实践和安全准则

    重点关注可读性、可维护性和性能方面。
  </BadExample>
</Examples>
```




## 记忆提示词目录

- `.docs/cmd/**/*.locale.md` - 是快捷命令文件, 它们注重任务效率优化
- `.docs/user/**/*.locale.md` - 是全局记忆文件, 它们通常更抽象
- `.docs/project/**/*.locale.md` - 是针对项目的模板, 虽抽象但更具有各项目的特色
- `.docs/ss/**.locale.md` - 是 "子代理", 它们很专业且单有领域
- `.docs/CLAUDE-**.locale.md` - 是针对 .docs/ 的 记忆提示词, 它们来帮助用户不断精进提示词以获得更好的效果
