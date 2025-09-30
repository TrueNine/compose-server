---
argument-hint: [locale_file_at_path]
allowed-tools: Read, Write, Edit, Glob, Grep, Bash
description: 优化 "Claude Code" 记忆提示词文件, 使得其记忆提示词文件让 "Claude Code" 更具可读性,达到更好的工作效果
---

`optimize-prompt` 优化 Claude.ai `$1` 记忆提示词文件的任务. 参数: `$1` (必需): 需要优化的提示词文件路径, `$2` (可选): 用户的具体需求或优化方向.

该任务会根据既定规则对 记忆提示词 进行结构优化、格式规范和内容精简. 对于非 `**.locale.*` 文件,会先翻译成中文 `**.locale.*` 文件再进行优化, 确保用户始终能够理解和控制提示词内容.

## 优化规则

### 语言选择规则
- **优先处理 `**.locale.*` 文件**: 当文件名包含 `**.locale.*` 时,直接执行流程
- **翻译非 .locale. 文件**: 如果用户传入的不是 `**.locale.*` 文件,则在其旁边翻译一份中文的 `**.locale.*` 文件,并对翻译后的 `**.locale.*` 文件进行优化
- **始终保持用户对提示词的感知能力**: 通过自动翻译和优化机制,确保用户始终能够理解和控制提示词内容

### 文档开头的简要描述
- 记忆提示词文件第一段应当包含一段 2-5 句的对整个文档内容的描述
- 简明扼要不艺术和表达

### 标题结构优化
- 标题嵌套级别不得大于3级
- 去除冗杂的标题嵌套
- 确保标题层次清晰

### 内容表达规范
- **禁止使用表情符号**:文档中严格禁止使用任何表情符号(emoji)保持专业性
- 使用简洁明了的书面语表达
- 保持文档风格一致性和专业性

### 示例编写规范

**XML标签体系**
采用结构化XML标签包裹示例, 优化AI解析效率和阅读体验:

**标签类型定义**:
- `<Example>` - 通用示例,展示标准用法
- `<Examples>` - 示例集合容器,包含多个相关示例
- `<GoodExample>` - 最佳实践示例,展示推荐做法
- `<BadExample>` - 反面教材示例,展示应避免的做法

**描述属性规范**:
- 所有示例标签支持 `description=""` 属性来说明示例的作用
- 描述应该简洁明了,突出示例的核心价值和学习要点

**对话机制规范**:
- `user:` - 用户输入内容
- `claude:` - Claude响应输出内容
- 支持独立的 `claude:` 标识纯输出场景

**格式化约束**:
- `<GoodExample>` 和 `<BadExample>` 标签仅可在 `<Examples>` 容器内使用
- 所有XML标签及其内容保持零缩进格式
- 标签与上方内容之间必须保留一个空行分隔,确保文档结构清晰
- **代码示例格式**:所有代码示例必须使用语言标识的代码块包裹,如:
  ```rust
  // Rust 代码
  ```

**内容精简原则**:
- 示例应当简短有效,突出核心要点
- 避免冗长的实现细节,专注展示概念
- 代码示例不超过20行,文本示例不超过5行
- 每个示例只展示一个关键概念或对比点

**BadExample 优化限制**:
- 优化时不对 `<BadExample>` 标签内的内容进行格式优化
- 除非 `<BadExample>` 中的内容不符合真正要表达的意义,否则保持原样
- `<BadExample>` 的目的是展示错误做法,包括错误的格式、标点、缩进等

### 核心结构要素
- **角色定义**:明确AI的身份和专业背景
- **任务描述**:清晰具体地说明要完成的任务
- **约束条件**:明确限制和要求
- **输出格式**:指定输出的结构和格式

### 注意力机制优化
- **核心要点限制**:每个提示词最多突出3个核心要点
- **避免注意力稀释**:过度使用强调格式(粗体、代码块等)会降低效果
- **位置策略**:将最关键信息放在开头和结尾

### 提示词长度优化
- **精简原则**:去除冗余描述,保留核心信息
- **必要细节**:保留关键的技术细节和约束条件
- **可读性**:合理分段,避免过长的段落

### 提示词文件结构要求
- **YAML 前置配置**: 文件开头可能包含 YAML 配置块,定义工具权限和基本描述
- **描述性文本**: 除了 YAML 配置外,还应包含一段文字描述,说明记忆提示词的用途和功能,大约 2-5 句
- **结构完整**: 确保提示词文件既有配置信息,又有功能说明
- ****: 

### 格式优化技巧
- **编码规范**:使用 UTF-8 编码确保兼容性
- **缩进规范**:统一使用 2 空格缩进
- **行结束符**:使用 LF 行结束符(非 CRLF)
- **格式一致性**:确保整个文档格式风格统一
- **标点符号规范**:禁止使用中文标点符号,统一使用英文标点符号

### 文件结构表示规范
- **禁止使用树形结构图**:不使用 ASCII 艺术风格的树形图来表示文件结构
- **使用缩进方式**:文件结构必须使用简单的缩进格式表示
- **清晰简洁**:确保结构清晰易读,避免过度复杂的表示方法

<Examples>
<GoodExample description="正确的文件结构表示方式(使用缩进)">
```text
docs/
  - `prompts/` - 提示词模板
    - `user/` - 全局用户提示词
    - `project/` - 项目级提示词
    - `slashcommands/` - 斜杠命令提示词
  - `qa/` - 问答文档
  - `references/` - 技术参考文档
  - `other/` - 其他文档(构建、Git、数据库等)
```
</GoodExample>
<BadExample description="错误的文件结构表示方式(使用树形结构图)">
docs/
├── prompts/             # 提示词模板
│   ├── user/            # 全局用户提示词
│   ├── project/         # 项目级提示词
│   └── slashcommands/   # 斜杠命令提示词
├── qa/                  # 问答文档
├── references/          # 技术参考文档
└── other/               # 其他文档(构建、Git、数据库等)
</BadExample>
</Examples>

### 明确性优化
- **避免歧义**:使用精确的词汇,避免模糊表达
- **具体化**:将抽象概念转化为具体要求
- **可执行性**:确保指令可以被准确理解和执行

### 约束条件明确化
- **必须包含**:明确列出必须满足的条件
- **禁止事项**:清晰说明不能做什么
- **边界条件**:定义处理的范围和限制

### 输出标准化
- **格式规范**:指定具体的输出格式(表格、列表、代码块等)
- **结构要求**:明确输出的组织结构
- **示例说明**:提供期望输出的示例

### 标点符号使用示例

<Examples>
<GoodExample description="正确使用英文标点符号">
# Role: Code Review Assistant

You are an expert code reviewer with 10+ years of experience. Your task is to:
1. Analyze code quality and identify potential issues
2. Provide actionable feedback for improvements
3. Ensure code follows best practices and security guidelines

Focus on readability, maintainability, and performance aspects.
</GoodExample>
<BadExample description="错误使用中文标点符号">
# Role: 代码审查助手

你是一位拥有10年以上经验的专家代码审查员。你的任务是:
1. 分析代码质量并识别潜在问题
2. 提供可操作的改进建议
3. 确保代码遵循最佳实践和安全准则

重点关注可读性、可维护性和性能方面。
</BadExample>
</Examples>

### 代码格式示例

<Examples>
<GoodExample description="正确的2空格缩进格式">
use std::collections::HashMap;

#[derive(Debug, Clone)]
pub struct ProcessedItem {
  pub id: String,
  pub name: String,
  pub value: f64,
}

pub fn process_data(data: &[HashMap<String, String>]) -> HashMap<String, Vec<ProcessedItem>> {
  let mut result = HashMap::new();

  if data.is_empty() {
    result.insert("status".to_string(), vec![]);
    result.insert("count".to_string(), vec![]);
    return result;
  }

  let mut processed = Vec::new();
  for item in data {
    if let Some(active) = item.get("active") {
      if active == "true" {
        if let (Some(id), Some(name), Some(value_str)) =
            (&item.get("id"), &item.get("name"), &item.get("value")) {
          if let Ok(value) = value_str.parse::<f64>() {
            processed.push(ProcessedItem {
              id: id.clone(),
              name: name.trim().to_string(),
              value,
            });
          }
        }
      }
    }
  }

  result.insert("status".to_string(), vec![]);
  result.insert("count".to_string(), vec![]);
  result
}
</GoodExample>
<BadExample description="错误的缩进和格式">
use std::collections::HashMap;

#[derive(Debug, Clone)]
pub struct ProcessedItem {
    pub id: String,
    pub name: String,
    pub value: f64,
}

pub fn process_data(data: &[HashMap<String, String>]) -> HashMap<String, Vec<ProcessedItem>> {
    let mut result = HashMap::new();

    if data.is_empty() {
        result.insert("status".to_string(), vec![]);
        result.insert("count".to_string(), vec![]);
        return result;
    }

    let mut processed = Vec::new();
    for item in data {
        if let Some(active) = item.get("active") {
            if active == "true" {
                if let (Some(id), Some(name), Some(value_str)) =
                    (&item.get("id"), &item.get("name"), &item.get("value")) {
                    if let Ok(value) = value_str.parse::<f64>() {
                        processed.push(ProcessedItem {
                            id: id.clone(),
                            name: name.trim().to_string(),
                            value,
                        });
                    }
                }
            }
        }
    }

    result.insert("status".to_string(), vec![]);
    result.insert("count".to_string(), vec![]);
    result
}
</BadExample>
</Examples>

### 提示词结构示例

<Examples>
<GoodExample description="清晰简洁的提示词结构">
# Code Generation Assistant

Generate clean, efficient, and well-documented code based on requirements.

## Key Guidelines
- Use meaningful variable and function names
- Include type hints for better code clarity
- Write docstrings for all public functions
- Follow the project's established patterns

## Output Format
```rust
/// Function implementation with proper documentation
pub fn function_name(param: ParamType) -> ReturnType {
  /// Brief description of the function.
  ///
  /// # Arguments
  /// * `param` - Description of the parameter
  ///
  /// # Returns
  /// Description of the return value
  ///
  /// # Examples
  /// ```
  /// let result = function_name(input_value);
  /// assert_eq!(result, expected_value);
  /// ```
  // Implementation here
}
```
</GoodExample>
<BadExample description="冗余复杂的提示词结构">
## 🤖 AI Code Generation Assistant v2.0.1 ###

### 📋 MISSION STATEMENT:
You are an advanced AI-powered code generation system designed to create high-quality, production-ready code solutions for enterprise applications.

### 🔧 TECHNICAL REQUIREMENTS:
- **Variable Naming Convention**: MUST utilize meaningful, self-documenting variable names that clearly express intent
- **Type Safety**: ALL function parameters MUST include comprehensive type annotations using the latest typing features
- **Documentation Standards**: EVERY public function REQUIRES extensive docstring documentation following Google/NumPy conventions
- **Pattern Consistency**: MUST rigidly adhere to existing architectural patterns without deviation

### 📤 COMPLEX OUTPUT SPECIFICATION:
The generated code should follow this exact structure:

```rust
// -*- coding: utf-8 -*-
//! Enterprise-grade function implementation with comprehensive documentation.
//! This module represents a critical business logic component.

pub fn elaborate_function_name_with_verbose_description(
    parameter_name: ParameterType,
) -> Result<ReturnTypeType, Box<dyn std::error::Error>> {
  /// This function performs the specified operation with high reliability.
  ///
  /// # Arguments
  /// * `parameter_name` - A detailed explanation of what this parameter represents,
  ///                     including its expected format, constraints, and usage patterns
  ///
  /// # Returns
  /// * `Result<ReturnTypeType, Box<dyn std::error::Error>>` - A comprehensive description of
  ///   the return value, including all possible return states, error conditions,
  ///   and data structure details
  ///
  /// # Errors
  /// * `ValueError` - Detailed explanation of when this error might occur
  /// * `TypeError` - Comprehensive list of scenarios leading to type errors
  ///
  /// # Examples
  /// ```
  /// match elaborate_function_name_with_verbose_description(input) {
  ///     Ok(result) => println!("Operation succeeded: {:?}", result),
  ///     Err(e) => eprintln!("Operation failed: {}", e),
  /// }
  /// ```
  // Implementation placeholder with extensive comments
  unimplemented!()
}
```
</BadExample>
</Examples>

---

## 文档类型说明

### 不同类型文档的特点和定位

- `docs/prompts/slashcommands/**.locale.md` - 是快捷命令文件, 它们注重任务效率优化
- `docs/prompts/user/**.locale.md` - 是全局记忆文件, 它们通常更抽象
- `docs/prompts/project/**.locale.md` - 是针对项目的模板, 虽抽象但更具有各项目的特色
- `docs/prompts/subagents/**.locale.md` - 是 "Clauee Code 子代理", 它们很专业且单有领域
- `docs/CLAUDE-**.locale.md` - 是针对 docs/ 的记忆提示词, 它们来帮助用户不断精进提示词以获得更好的效果
