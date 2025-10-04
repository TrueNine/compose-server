
# 交流语言使用规范
- 用户交流: 所有面向用户的输出统一使用 `简体中文`, 保留行业常见英文术语, 句式遵循英语逻辑, 不搞文绉绉.
- 代码开发: 代码、注释、命名全部采用英文, 禁止出现中文标识, 入场首日即开始执行。
- 内部思考: `Thinking` 和 `Tooling` 段必须美式英语, 示例如 `Thinking: Validate payload schema`, 禁止出现 `Thinking: 检查文件` 这种混搭。
- 会话自检: 发送前检查 `Thinking` 与 `Tooling` 是否混入中文, 若发现立即改成英文; 用户可见输出保持简体中文。
- 文化取向: 记住用户母语是中文, 但工作流程全程对齐美国工程文化, 除用户沟通外一律使用美式英语处理事务。
- 严格禁止: `Thinking` 字段出现中文字符 => 直接视为违规, 从 `onboarding` 起就开始抓。
- 提示词风格: 只要 `Markdown` 合规就行, 内容保持技术向和精炼, 不为排版对齐或辞藻堆砌浪费时间。
- `**/*.locale.md` 文件: 所有 `**/*.locale.md` 统一用英式中文书写, 保持英文逻辑和术语直译, 全文执行。




# 项目工具链配置约束

## 工具链优先级
- 采用顺序: 1) 根目录配置文件; 2) `.tool-versions` 或 `mise`; 3) `README` 指南; 4) 现有脚本与 `CI`。

```xml
<Examples>
  <GoodExample description="示例: 正确识别并使用项目工具链"
               userInput="帮我运行测试">
    <Tooling name="Search" params:pattern="Cargo.toml">
      Locate Cargo.toml within the workspace
    </Tooling>
    <Tooling name="Bash" params:command="test -f Cargo.toml">
      Confirm Cargo.toml exists at the repository root
    </Tooling>
  </GoodExample>

  <BadExample description="未调查即假设工具链"
             userInput="帮我运行测试">
    <Tooling name="Bash"
             params:command="npm test" />
  </BadExample>
</Examples>
```




## 命令生成规范
- 构建: 根据工具链选择 `cargo build` / `npm run build` / `pip install` 等。
- 测试: 使用 `cargo test` / `npm test` / `pytest` 等, 不得自创命令。
- 格式化: 遵循项目脚本, 如 `cargo fmt`, `prettier`, `black`。
- 检查: 根据语言运行 `cargo clippy`, `eslint`, `flake8` 等。




# 代码质量标准

## 统一格式规范
- 缩进: 固定 `2 spaces`.
- 编码: `UTF-8`.
- 行末: `LF`.

```xml
<Examples>
  <GoodExample description="示例: 2 空格缩进的正确格式">
    fn main() {
      println!("Hello World");
    }
  </GoodExample>

  <BadExample description="反例: 4 空格缩进导致格式错误">
    fn main() {
        println!("Hello World");
    }
  </BadExample>
</Examples>
```




## 命名规范
- 优先顺序: `PascalCase` 或 `camelCase` -> `snake_case` -> 避免 `kebab-case` (除非语言强制)。

```xml
<Examples>
  <GoodExample description="类型采用 PascalCase">
    struct UserAccount;
  </GoodExample>

  <GoodExample description="变量采用 camelCase">
    let userName = "john";
  </GoodExample>

  <GoodExample description="变量可接受 snake_case">
    let user_count = 42;
  </GoodExample>

  <GoodExample description="Rust 模块使用 snake_case">
    mod user_service;
  </GoodExample>

  <BadExample description="变量使用 kebab-case">
    let user-name = "john";
  </BadExample>

  <BadExample description="类型使用 kebab-case">
    struct user-account;
  </BadExample>
</Examples>
```




## 代码风格约束

- 注释应当置于语句上方, 禁止行尾补充, 以免拉长代码行并降低可读性
- 条件语句与循环体必须显式使用大括号, 避免因省略而引入严重漏洞

```xml
<Examples>
  <GoodExample description="条件分支始终使用大括号">
    if (is_ready) {
      handle_ready();
    }
  </GoodExample>

  <BadExample description="省略大括号导致逻辑失控">
    if (is_ready)
      handle_ready();
      finalize();
  </BadExample>

  <BadExample description="行内注释拉长代码行">
    let total = price * quantity; // skip tax for legacy orders
  </BadExample>
  <GoodExample description="正确注释方式">
    // skip tax for legacy orders
    let total = price * quantity;
  </GoodExample>
</Examples>
```





## 代码编写技巧

### `Guard Clauses` & `Early Return`
要求使用 `guard clause` 与 `early return` 减少嵌套层级。

```xml
<Examples>
  <GoodExample description="使用 guard clause 降低嵌套">
    fn process_user(user: Option<&User>) -> Option<ProcessedUser> {
      let user = user?;
      if !user.is_active { return None; }
      if user.age < 18 { return None; }
      handle_adult_user(user)
    }
  </GoodExample>

  <BadExample description="深层嵌套的写法">
    fn process_user(user: Option<&User>) -> Option<ProcessedUser> {
      if let Some(user) = user {
        if user.is_active {
          if user.age >= 18 {
            return handle_adult_user(user);
          }
        }
      }
      None
    }
  </BadExample>
</Examples>
```

### 多条件判断优化
- 条件数量≥3 时, 统一改用 `switch` / `match` 或查表方案替代 `if-else` 链。
- 目标: 提升可读性和可维护性, 减少重复判断。

```xml
<Examples>
  <GoodExample description="match 分支覆盖多条件">
    fn get_error_message(status_code: u16) -> &'static str {
      match status_code {
        403 => "Permission denied, cannot access this resource",
        404 => "Requested resource does not exist",
        500 => "Internal server error, please try again later",
        code if code >= 500 => "Server error, please try again later",
        _ => "Unknown error"
      }
    }
  </GoodExample>

  <GoodExample description="查表替代多分支">
    use std::collections::HashMap;

    fn get_error_message_lookup(status_code: u16) -> &'static str {
      let error_messages: HashMap<u16, &'static str> = [
        (403, "Permission denied, cannot access this resource"),
        (404, "Requested resource does not exist"),
        (500, "Internal server error, please try again later")
      ].iter().cloned().collect();

      error_messages.get(&status_code).unwrap_or(
        &if status_code >= 500 { "Server error, please try again later" }
        else { "Unknown error" }
      )
    }
  </GoodExample>

  <BadExample description="大量 if-else 链处理多条件">
    fn get_error_message(status_code: u16) -> &'static str {
      let mut error_message = "Unknown error";

      if status_code == 403 {
        error_message = "Permission denied, cannot access this resource";
      } else if status_code == 404 {
        error_message = "Requested resource does not exist";
      } else if status_code == 500 {
        error_message = "Internal server error, please try again later";
      } else if status_code >= 500 {
        error_message = "Server error, please try again later";
      }

      error_message
    }
  </BadExample>
</Examples>
```





## 代码错误检测
- 每次完成功能后调用项目现成的 `diagnostic` 或 `lint` 指令捕获语法与类型问题。
- 修改文件即刻重新诊断, 防止错误堆积。





# 开发原则与约束

## 语言策略
- 技术选型与架构调整默认优先静态类型语言栈 (如 Rust、TypeScript、Go、Java、Kotlin), 以保证类型安全与长期可维护性。
- 即便现有项目建立在动态语言上, 提出方案时也要规划向静态类型迁移的路线, 包含阶段目标与风险提示。
- 禁止主动扩充新的动态语言组件; 若用户坚持动态语言, 需再次确认并完整记录潜在风险。




## 禁止重复造轮子
- 开发前先调查现有功能与架构。
- 强制复用现有组件、工具或函数, 不得重新实现。
- 优先审视并优化现有实现与提示词, 通过补充测试、提升可维护性或强化可读性来获得增量价值。
- 默认策略: 在现有基础上扩展能力而非重写。

### 调查工作流程
1. 全面搜索: 使用 `Search` 工具探索关键词, 用 `Glob` 查看目录结构, 以 `Read` 深入关键文件。
2. 架构分析: 理解设计模式与编码风格, 定位可复用组件与最佳扩展点。
3. 复用策略: 首选扩展已有类/函数, 其次组合现有组件, 最后才创建新组件并提供充分理由。

```xml
<Examples>
  <GoodExample description="开发前调查并复用现有代码"
               userInput="帮我实现一个用户认证功能">
    先搜索认证相关代码。
    <Tooling name="Search"
             params:words="auth" />
    <Tooling name="Read"
             params:path="AuthService" />
    选择扩展现有组件, 避免重造轮子:

    ```rust
    // 扩展现有 AuthService
    impl AuthService {
      pub async fn login(&self, credentials: LoginCredentials) -> Result<AuthResult, AuthError> {
        // ... existing implementation
      }

      // 新增多因素认证能力
      pub async fn enable_two_factor(&self, user_id: &str) -> Result<TwoFactorSetup, AuthError> {
        let user = self.validate_user(user_id).await?;
        self.setup_two_factor(&user).await
      }
    }
    ```
  </GoodExample>

  <BadExample description="跳过调查直接重写功能"
             userInput="帮我实现一个用户认证功能">
    跳过调查直接重写, 违规:
    <Tooling name="Write"
             params:path="NewAuthSystem" />
    ```rust
    struct NewAuthSystem;

    impl NewAuthSystem {
      async fn authenticate_user(&self, username: &str, password: &str) -> Result<User, AuthError> {
        unimplemented!()
      }
    }
    ```
  </BadExample>
</Examples>
```




## 文件管理原则
- 优先编辑现有文件, 避免创建新文件。
- 新文件必须有充分理由并符合架构规范。
- 遵循既有目录与模块组织方式。

### 文件创建决策标准
合理创建新文件:
- 模块功能独立且规模较大 (>=100 行)。
- 职责与现有文件明显不同。
- 需要独立测试文件。
- 吻合项目的模块化设计。

避免创建新文件:
- 仅包含少量函数或常量。
- 功能与现有文件高度相关。
- 只是为了避免单文件过长 (除非确有必要)。
- 破坏原有组织结构。

```xml
<Examples>
  <GoodExample description="在现有文件内扩展功能"
               userInput="添加一个新的工具函数来格式化日期">
    将日期格式化函数合并进现有工具文件。
    <Tooling name="Write"
             params:path="src/utils/helpers.rs" />
    ```rust
    use chrono::{DateTime, Local};

    // 在 helpers.rs 中新增函数
    pub fn format_date(date: DateTime<Local>, format: &str) -> String {
      date.format(format).to_string()
    }

    // 保持与现有函数风格一致
    pub fn format_currency(amount: f64) -> String { /* ... */ "".to_string() }
    pub fn format_phone_number(phone: &str) -> String { /* ... */ "".to_string() }
    ```
  </GoodExample>

  <BadExample description="反例: 不必要地创建新文件"
              userInput="添加一个新的工具函数来格式化日期">
    不必要地拆出新文件:
    <Tooling name="Write"
             params:path="src/utils/date_utils.rs"
             description="不必要的文件创建"/>
    ```rust
    use chrono::{DateTime, Local};

    pub fn format_date(date: DateTime<Local>, format: &str) -> String {
      date.format(format).to_string()
    }
    ```
  </BadExample>
</Examples>
```




## 错误处理透明化原则
- 禁止掩盖或镇压任何错误与警告。
- 禁止镇压警告、私自捕获不抛出、空异常块、忽略错误码、隐藏异常详情、篡改检查器配置。

### 错误处理规范
- 透明: 所有错误/警告完整暴露给用户或调用层。
- 追溯: 保留完整堆栈与上下文。
- 责任: 由调用层决定如何处理, 不得在底层静默吞掉。

### 错误处理示例

```xml
<Examples>
  <GoodExample description="完全透明">
    fn process_file(path: &str) -> Result<ProcessedData, ProcessingError> {
      let file = std::fs::File::open(path)
        .map_err(|e| ProcessingError::FileOpenError {
          path: path.to_string(),
          source: e
        })?;

      let result = parse_file_content(&file)
        .map_err(|e| ProcessingError::ParseError {
          path: path.to_string(),
          source: e
        })?;

      Ok(result)
    }
  </GoodExample>

  <BadExample description="掩盖错误">
    fn process_file(path: &str) -> Option<ProcessedData> {
      let file = match std::fs::File::open(path) {
        Ok(f) => f,
        Err(_) => return None,
      };

      match parse_file_content(&file) {
        Ok(result) => Some(result),
        Err(e) => {
          eprintln!("Parse error occurred: {}", e);
          None
        }
      }
    }
  </BadExample>
</Examples>
```

### 警告处理示例

```xml
<Examples>
  <GoodExample description="必须传递给调用者">
    fn validate_config(config: &Config) -> Result<(), Vec<ValidationWarning>> {
      let mut warnings = Vec::new();

      if config.timeout < 1000 {
        warnings.push(ValidationWarning::ShortTimeout(config.timeout));
      }

      if !warnings.is_empty() {
        return Err(warnings);
      }

      Ok(())
    }
  </GoodExample>

  <BadExample description="镇压警告">
    fn validate_config(config: &Config) {
      if config.timeout < 1000 {
        // 镇压警告 - 禁止
        // println!("Warning: timeout is very short");
      }

      // 未告知调用者潜在问题
    }
  </BadExample>
</Examples>
```

### 异常处理示例

- 保持异常透明, 优先让异常向上抛出
- 如需捕获, 必须补充上下文并重新抛出或返回错误对象, 禁止静默吞掉
- 优先复用现有异常类型, 避免随意创建新异常导致维护成本上升

```xml
<Examples>
  <GoodExample description="复用现有异常并补充上下文">
    try {
      riskyOperation();
    } catch (Exception e) {
      throw new IOException("Unable to finish task", e);
    }
  </GoodExample>

  <BadExample description="静默吞掉异常导致信息丢失">
    try {
      riskyOperation();
    } catch (Exception ignored) {
    }
  </BadExample>
</Examples>
```




# 严格验证机制

## 完成验证要求
- 严禁未验证就声称完成。
- 必须以真实验证动作而非主观判断佐证结果。
- 进度反馈: 任务尚未完成时必须直接说明当前进度与阻塞点, 禁止以“我将以更简单的方式实现”等空泛承诺搪塞, 更不得虚构完成结果; 如卡住应主动请求开发者指导。

## 验证步骤清单
1. 代码质量验证:
  - 运行项目定义的 `diagnostic` / `lint` 命令检查语法和类型问题。
  - 探测项目工具链, 使用对应检查命令:
    - `Rust`: `cargo clippy`, `cargo fmt --check`。
    - `Node.js`: 依 `package.json` 运行 `lint` 脚本。
    - `Gradle`: `build.gradle.kts`, `settings.gradle.kts` 等。
2. 功能验证:
  - 按工具链运行测试:
    - `Rust`: `cargo test`。
    - `Node.js`: `yarn test` / `pnpm test`。
    - `Gradle`: `./gradlew test`。
  - 通过 `Bash` 工具执行必要的手动验证。
  - 确认所有修改按预期运行。
3. 完整性验证:
  - 检查是否覆盖用户需求。
  - 确认 `TodoWrite` 任务全部标记为 `completed`。
  - 验证改动未破坏既有功能。





## 验证失败处理
- 如实报告发现的问题.
- 透明说明失败原因.
- 给出后续修复计划.

```xml
<Examples>
  <GoodExample description="全面执行验证流程"
               userInput="帮我修复这个登录功能的bug">
    修复后执行完整验证。
    <Tooling name="Bash" params:command="diagnostic">
      Run project-defined diagnostic command to surface syntax issues
    </Tooling>
    <Tooling name="Bash" params:command="cargo test">
      Execute `cargo test` aligned with the detected Cargo configuration
    </Tooling>
    <Tooling name="Bash" params:command="manual login flow">
      Drive a manual login flow check via Bash to confirm behaviour
    </Tooling>
    全部验证通过:

    - ✅ 无语法或类型错误。
    - ✅ `cargo test` 全绿。
    - ✅ 手测登录流程正常。

    登录问题已修复并验证。
  </GoodExample>

  <BadExample description="未验证即声称完成"
             userInput="帮我修复这个登录功能的bug">
    未验证即声称修复完成, 违规。
  </BadExample>
</Examples>
```




# 提示词编写规范

- `**.locale.md` 属于英式中文提示词文件，面向用户阅读，语言需紧贴美式英语的逻辑与思路，专业术语直接保留英文原文。
- 这些 `locale` 文件常作为 `AI Agent` 快捷命令、子代理提示词或 `memory prompt file` 的本地化版本，内容必须精确映射英文主稿。
- `AGENTS.md` 与 `CLAUDE.md` 分布在仓库各处，通常采用英文撰写，可能因维护不及时而混入中文语感或过时信息，仅可作为旁证资料。
- 示例多使用 `xml` 结构呈现，具备高参考价值，遵循示例时优先理解其结构化意图。
- 作为 `AI Agent` 协助用户更新或撰写此类文件时，要假设用户是一名程序员，可能正面临混乱项目或陈旧文档，请主动修正并补齐缺漏。
- 不要直接照搬现有的 `**.locale.md` 内容；请以英文原稿为权威来源，将其翻译成标准美式英语逻辑下的英式中文，确保 locale 版本准确可读。
- 当用户提出新的规则或想法时, 需立刻在当前正在编辑的 locale 文件中落实更新, 避免延后处理。
