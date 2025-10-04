# Communication Language Usage Standards
- User communication: All user-facing output uniformly uses `Simplified Chinese`, retaining common industry English terms, sentence structure follows English logic, avoiding overly formal language.
- Code development: Code, comments, and naming all use English, prohibiting Chinese identifiers, effective from day one.
- Internal thinking: `Thinking` and `Tooling` sections must use American English, examples like `Thinking: Validate payload schema`, prohibiting mixed usage like `Thinking: 检查文件`.
- Session self-check: Before sending, check if `Thinking` and `Tooling` contain Chinese, immediately change to English if found; user-visible output remains Simplified Chinese.
- Cultural orientation: Remember user's native language is Chinese, but entire workflow aligns with American engineering culture, using American English for all matters except user communication.
- Strict prohibition: Chinese characters in `Thinking` field => directly considered violation, enforced from `onboarding`.
- Prompt style: As long as `Markdown` is compliant, content remains technical and concise, not wasting time on formatting alignment or flowery language.
- `**/*.locale.md` files: All `**/*.locale.md` uniformly use English-style Chinese writing, maintaining English logic and direct translation of terminology, applied throughout.




# Project Toolchain Configuration Constraints

## Toolchain Priority
- Adoption order: 1) Root directory configuration files; 2) `.tool-versions` or `mise`; 3) `README` guides; 4) Existing scripts and `CI`.

```xml
<Examples>
  <GoodExample description="Example: Correctly identify and use project toolchain"
               userInput="Help me run tests">
    <Tooling name="Search" params:pattern="Cargo.toml">
      Locate Cargo.toml within the workspace
    </Tooling>
    <Tooling name="Bash" params:command="test -f Cargo.toml">
      Confirm Cargo.toml exists at the repository root
    </Tooling>
  </GoodExample>

  <BadExample description="Assume toolchain without investigation"
             userInput="Help me run tests">
    <Tooling name="Bash"
             params:command="npm test" />
  </BadExample>
</Examples>
```




## Command Generation Standards
- Build: Choose `cargo build` / `npm run build` / `pip install` etc. based on toolchain.
- Test: Use `cargo test` / `npm test` / `pytest` etc., do not create custom commands.
- Format: Follow project scripts, such as `cargo fmt`, `prettier`, `black`.
- Check: Run `cargo clippy`, `eslint`, `flake8` etc. based on language.




# Code Quality Standards

## Unified Format Standards
- Indentation: Fixed `2 spaces`.
- Encoding: `UTF-8`.
- Line endings: `LF`.

```xml
<Examples>
  <GoodExample description="Example: Correct format with 2-space indentation">
    fn main() {
      println!("Hello World");
    }
  </GoodExample>

  <BadExample description="Counter-example: 4-space indentation causes format error">
    fn main() {
        println!("Hello World");
    }
  </BadExample>
</Examples>
```




## Naming Conventions
- Priority order: `PascalCase` or `camelCase` -> `snake_case` -> avoid `kebab-case` (unless language enforces).

```xml
<Examples>
  <GoodExample description="Types use PascalCase">
    struct UserAccount;
  </GoodExample>

  <GoodExample description="Variables use camelCase">
    let userName = "john";
  </GoodExample>

  <GoodExample description="Variables accept snake_case">
    let user_count = 42;
  </GoodExample>

  <GoodExample description="Rust modules use snake_case">
    mod user_service;
  </GoodExample>

  <BadExample description="Variables use kebab-case">
    let user-name = "john";
  </BadExample>

  <BadExample description="Types use kebab-case">
    struct user-account;
  </BadExample>
</Examples>
```




## Code Style Constraints

- Comments should be placed above statements, prohibiting end-of-line additions to avoid lengthening code lines and reducing readability
- Conditional statements and loop bodies must explicitly use braces, avoiding serious vulnerabilities from omission

```xml
<Examples>
  <GoodExample description="Conditional branches always use braces">
    if (is_ready) {
      handle_ready();
    }
  </GoodExample>

  <BadExample description="Omitting braces causes logic loss of control">
    if (is_ready)
      handle_ready();
      finalize();
  </BadExample>

  <BadExample description="Inline comments lengthen code lines">
    let total = price * quantity; // skip tax for legacy orders
  </BadExample>
  <GoodExample description="Correct comment style">
    // skip tax for legacy orders
    let total = price * quantity;
  </GoodExample>
</Examples>
```




## Code Writing Techniques

### `Guard Clauses` & `Early Return`
Require using `guard clause` and `early return` to reduce nesting levels.

```xml
<Examples>
  <GoodExample description="Use guard clause to reduce nesting">
    fn process_user(user: Option<&User>) -> Option<ProcessedUser> {
      let user = user?;
      if !user.is_active { return None; }
      if user.age < 18 { return None; }
      handle_adult_user(user)
    }
  </GoodExample>

  <BadExample description="Deeply nested approach">
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

### Multi-condition Judgment Optimization
- When condition count ≥3, uniformly switch to `switch` / `match` or lookup table solutions instead of `if-else` chains.
- Goal: Improve readability and maintainability, reduce repeated judgments.

```xml
<Examples>
  <GoodExample description="match branches cover multiple conditions">
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

  <GoodExample description="Lookup table replaces multiple branches">
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

  <BadExample description="Extensive if-else chains handle multiple conditions">
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




## Code Error Detection
- After completing functionality, call project's existing `diagnostic` or `lint` commands to catch syntax and type issues.
- Re-diagnose immediately after modifying files to prevent error accumulation.




# Development Principles and Constraints

## Language Strategy
- Technology selection and architecture adjustments default to prioritizing statically typed language stacks (such as Rust, TypeScript, Go, Java, Kotlin) to ensure type safety and long-term maintainability.
- Even if existing projects are built on dynamic languages, when proposing solutions, also plan migration routes to static typing, including phase goals and risk warnings.
- Prohibit actively expanding new dynamic language components; if users insist on dynamic languages, reconfirm and fully document potential risks.




## Prohibit Reinventing the Wheel
- Investigate existing functionality and architecture before development.
- Force reuse of existing components, tools, or functions, do not reimplement.
- Prioritize reviewing and optimizing existing implementations and prompts, gaining incremental value through adding tests, improving maintainability, or strengthening readability.
- Default strategy: Extend capabilities on existing foundation rather than rewrite.

### Investigation Workflow
1. Comprehensive search: Use `Search` tool to explore keywords, use `Glob` to view directory structure, use `Read` to dive into key files.
2. Architecture analysis: Understand design patterns and coding styles, locate reusable components and optimal extension points.
3. Reuse strategy: First choice is extending existing classes/functions, second is combining existing components, last resort is creating new components with sufficient justification.

```xml
<Examples>
  <GoodExample description="Investigate and reuse existing code before development"
               userInput="Help me implement a user authentication feature">
    First search for authentication-related code.
    <Tooling name="Search"
             params:words="auth" />
    <Tooling name="Read"
             params:path="AuthService" />
    Choose to extend existing components, avoid reinventing the wheel:

    ```rust
    // Extend existing AuthService
    impl AuthService {
      pub async fn login(&self, credentials: LoginCredentials) -> Result<AuthResult, AuthError> {
        // ... existing implementation
      }

      // Add multi-factor authentication capability
      pub async fn enable_two_factor(&self, user_id: &str) -> Result<TwoFactorSetup, AuthError> {
        let user = self.validate_user(user_id).await?;
        self.setup_two_factor(&user).await
      }
    }
    ```
  </GoodExample>

  <BadExample description="Skip investigation and directly rewrite functionality"
             userInput="Help me implement a user authentication feature">
    Skip investigation and directly rewrite, violation:
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




## File Management Principles
- Prioritize editing existing files, avoid creating new files.
- New files must have sufficient justification and comply with architecture standards.
- Follow existing directory and module organization methods.

### File Creation Decision Criteria
Reasonable to create new files:
- Module functionality is independent and large scale (>=100 lines).
- Responsibilities are clearly different from existing files.
- Independent test files are needed.
- Aligns with project's modular design.

Avoid creating new files:
- Only contains few functions or constants.
- Functionality is highly related to existing files.
- Just to avoid single file being too long (unless absolutely necessary).
- Breaks original organizational structure.

```xml
<Examples>
  <GoodExample description="Extend functionality within existing file"
               userInput="Add a new utility function to format dates">
    Merge date formatting function into existing utility file.
    <Tooling name="Write"
             params:path="src/utils/helpers.rs" />
    ```rust
    use chrono::{DateTime, Local};

    // Add new function in helpers.rs
    pub fn format_date(date: DateTime<Local>, format: &str) -> String {
      date.format(format).to_string()
    }

    // Maintain consistency with existing function styles
    pub fn format_currency(amount: f64) -> String { /* ... */ "".to_string() }
    pub fn format_phone_number(phone: &str) -> String { /* ... */ "".to_string() }
    ```
  </GoodExample>

  <BadExample description="Counter-example: Unnecessarily creating new file"
              userInput="Add a new utility function to format dates">
    Unnecessarily split into new file:
    <Tooling name="Write"
             params:path="src/utils/date_utils.rs"
             description="Unnecessary file creation"/>
    ```rust
    use chrono::{DateTime, Local};

    pub fn format_date(date: DateTime<Local>, format: &str) -> String {
      date.format(format).to_string()
    }
    ```
  </BadExample>
</Examples>
```




## Error Handling Transparency Principle
- Prohibit covering up or suppressing any errors and warnings.
- Prohibit suppressing warnings, privately catching without throwing, empty exception blocks, ignoring error codes, hiding exception details, tampering with checker configuration.

### Error Handling Standards
- Transparent: All errors/warnings fully exposed to users or calling layer.
- Traceable: Preserve complete stack and context.
- Responsibility: Decided by calling layer how to handle, cannot silently swallow at bottom layer.

### Error Handling Examples

```xml
<Examples>
  <GoodExample description="Fully transparent">
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

  <BadExample description="Covering up errors">
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

### Warning Handling Examples

```xml
<Examples>
  <GoodExample description="Must pass to caller">
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

  <BadExample description="Suppressing warnings">
    fn validate_config(config: &Config) {
      if config.timeout < 1000 {
        // Suppressing warning - prohibited
        // println!("Warning: timeout is very short");
      }

      // Not informing caller of potential issues
    }
  </BadExample>
</Examples>
```

### Exception Handling Examples

- Maintain exception transparency, prioritize letting exceptions bubble up
- If catching is needed, must add context and re-throw or return error object, prohibit silently swallowing
- Prioritize reusing existing exception types, avoid arbitrarily creating new exceptions causing maintenance cost increase

```xml
<Examples>
  <GoodExample description="Reuse existing exceptions and add context">
    try {
      riskyOperation();
    } catch (Exception e) {
      throw new IOException("Unable to finish task", e);
    }
  </GoodExample>

  <BadExample description="Silently swallowing exceptions causes information loss">
    try {
      riskyOperation();
    } catch (Exception ignored) {
    }
  </BadExample>
</Examples>
```




# Strict Verification Mechanism

## Completion Verification Requirements
- Strictly prohibit claiming completion without verification.
- Must use real verification actions rather than subjective judgment to support results.
- Progress feedback: When tasks are not yet complete, must directly state current progress and blocking points, prohibit using vague promises like "I will implement in a simpler way" to evade, and must not fabricate completion results; if stuck, should actively request developer guidance.

## Verification Steps Checklist
1. Code quality verification:
  - Run project-defined `diagnostic` / `lint` commands to check syntax and type issues.
  - Detect project toolchain, use corresponding check commands:
    - `Rust`: `cargo clippy`, `cargo fmt --check`.
    - `Node.js`: Run `lint` script according to `package.json`.
    - `Gradle`: `build.gradle.kts`, `settings.gradle.kts` etc.
2. Functionality verification:
  - Run tests according to toolchain:
    - `Rust`: `cargo test`.
    - `Node.js`: `yarn test` / `pnpm test`.
    - `Gradle`: `./gradlew test`.
  - Execute necessary manual verification through `Bash` tool.
  - Confirm all modifications run as expected.
3. Completeness verification:
  - Check if user requirements are covered.
  - Confirm all `TodoWrite` tasks are marked as `completed`.
  - Verify changes do not break existing functionality.




## Verification Failure Handling
- Honestly report discovered issues.
- Transparently explain failure reasons.
- Provide subsequent repair plans.

```xml
<Examples>
  <GoodExample description="Comprehensively execute verification process"
               userInput="Help me fix this login functionality bug">
    After fixing, execute complete verification.
    <Tooling name="Bash" params:command="diagnostic">
      Run project-defined diagnostic command to surface syntax issues
    </Tooling>
    <Tooling name="Bash" params:command="cargo test">
      Execute `cargo test` aligned with the detected Cargo configuration
    </Tooling>
    <Tooling name="Bash" params:command="manual login flow">
      Drive a manual login flow check via Bash to confirm behaviour
    </Tooling>
    All verification passed:

    - ✅ No syntax or type errors.
    - ✅ `cargo test` all green.
    - ✅ Manual login flow test normal.

    Login issue fixed and verified.
  </GoodExample>

  <BadExample description="Claim completion without verification"
             userInput="Help me fix this login functionality bug">
    Claim fix completion without verification, violation.
  </BadExample>
</Examples>
```




# Prompt Writing Standards

- `**.locale.md` are English-style Chinese prompt files, user-facing, language needs to closely follow American English logic and thinking, professional terms directly retain English original text.
- These `locale` files are often used as localized versions of `AI Agent` quick commands, sub-agent prompts, or `memory prompt files`, content must precisely map to English master copy.
- `AGENTS.md` and `CLAUDE.md` are distributed throughout the repository, usually written in English, may mix Chinese language sense or outdated information due to untimely maintenance, only serve as supporting materials.
- Examples mostly use `xml` structure presentation, have high reference value, when following examples, prioritize understanding their structured intent.
- As `AI Agent` assisting users to update or write such files, assume user is a programmer, may be facing chaotic projects or outdated documentation, please actively correct and fill gaps.
- Do not directly copy existing `**.locale.md` content; please use English master copy as authoritative source, translate it into English-style Chinese under standard American English logic, ensuring locale version is accurate and readable.
- When users propose new rules or ideas, must immediately implement updates in the currently editing locale file, avoid delayed processing.
