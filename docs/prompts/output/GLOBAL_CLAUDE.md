# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in all Repository.

---

All languages must follow general rules, and specific languages must follow specific language rules based on general rules.

# General Standards

**Mandatory Rules**

1. Always respond in **Simplified Chinese**, even if the user inputs extensive English prompts, responses should still be in Simplified Chinese
2. Prohibit writing any sample code for user consumption, even if needed for temporary testing, it must be deleted immediately after task completion
3. Strictly prohibit solving problems by simplifying them
4. Strictly prohibit solving compilation errors and runtime errors by downgrading dependency versions, reducing test assertions, using constant test conditions, etc.
5. Strictly prohibit ignoring exceptions or any behavior that hides exceptions
6. Strictly prohibit exposing API keys, passwords, and tokens in code
7. Should actively refactor and fix code appearing in context according to rules
8. Should actively use logging to improve records, proactively supplement missing logs
9. Allow adding logs during unit test debugging to assist in problem resolution
10. Must maximize the use of `early return` technique to reduce code nesting levels
11. Strictly prohibit generating summary documentation files and other unnecessary operations after dialogue or task completion
12. Strictly prohibit the appearance of the following disgusting and decadent terms anywhere except in third-party library calls
  + `util`, `utils`
  + `helper`, `helpers`
  + `tool`, `tools`

13. Strictly prohibit using `@Suppress`/`// @ts-ignore` and other annotations to suppress warnings

**Progressive Development Methodology**

- Adopt test-driven development combined with progressive development approach
- **Baby Steps Development**: Write only small amounts of code each time, immediately verify with unit tests
- **Rapid Feedback Loop**: Develop a bit and test a bit, never write large amounts of code at once
- **Individual Verification Principle**: When writing unit tests for multiple pieces of code, write and verify one by one, avoid batch writing followed by unified verification
- **Risk Minimization**: Ensure code quality through frequent small-scale verification, avoid error accumulation
- **Nested Test Organization**: Use appropriate grouping, avoid large numbers of independent test methods at root level
- Strictly prohibit test methods without assertions, immediately supplement assertions when found

**Logging Standards**

- When there are no logs, should use more logging to assist in online problem troubleshooting and obtaining more contextual information
- When troubleshooting problems, can add more logs to source code to obtain more detailed contextual information
- Logs should only use English for recording, if other languages are found they should be immediately corrected to English
- Focus on recording detailed logs of third-party library calls and external APIs, including request parameters, response results, duration, etc.
- Use structured log format: `log.info("API call completed - endpoint: {}, status: {}, duration: {}ms", endpoint, status, duration)`
- Record key contextual information: user ID, request ID, operation type, result status, exception details
- Avoid recording sensitive information: passwords, API keys, ID numbers, phone numbers, etc.
- Record logs at key points such as external calls, database operations, file operations
- Use placeholder format to avoid string concatenation, keep log messages concise and clear
- Use log levels appropriately, avoid outputting too many DEBUG logs in production environment

**Comment Rules**

- Documentation comments: Must use English comments
- Internal code comments: Must use Chinese comments
- Strictly prohibit using end-of-line comments

**Test Organization Best Practices:**

- Create main groupings for each tested class/function/variable/method
- Subdivide by scenarios: normal cases, exception cases, boundary cases
  + Kotlin example: `@Nested inner class CreateUser { @Test fun should_create_successfully() {} }`

**Code Style**

- Code indentation: Use 2 spaces for indentation
- Code line breaks: Each file must retain trailing newlines
- File encoding: Must use UTF-8 encoding
- File line endings: Must use LF line endings
- Line length: Each line must not exceed 160 characters
- Configuration reference: Refer to the `.editorconfig` configuration in the project root directory

# Specific Language Conventions

**SQL Rules**

- Strictly prohibit using any comments in SQL
- Check if existing queries use parameterization
- Consistently use snake_case naming, even in strings appearing in other languages

**JVM Rules**

1. Strictly prohibit using `@DisplayName` annotation in test code
2. In spring/quarkus, strictly prohibit using framework-specific annotations, for example: `@Autowired` must be replaced with `@Resource`
3. Use the maximum extent of new features available in the project's JDK version as much as possible
4. Prohibit using `@DisplayName` annotation in unit tests
5. Unit test method names should use English naming with underscore separation
6. Prohibit unit test method names from starting with `test_` or `should_`
7. When logging, use the format `log.info("message param1: {}, param2{}")`
8. Prohibit logging detailed sensitive information, such as: passwords, ID numbers, API keys, phone numbers, etc.
9. Interfaces must start with "I" prefix
10. All ways of obtaining slf4j Logger instances must be static, private, and final

**Java Rules**

All JVM platform programming languages: java, kotlin, groovy, scala must follow these principles

1. Can use `import xxx.*` imports to reduce code volume, leave subsequent processing to IDE
2. Use JDK new features as much as possible
3. Variable declarations should use `final var` as much as possible
4. Actively use lambda expressions
5. Strictly prohibit using `System.out.println` for output logging

**Kotlin Rules**

1. Prefer using val for immutable variable declarations
2. Avoid !! operator, use ?. or let{}
3. Use data classes instead of multi-parameter functions
4. Strictly prohibit using `println` for output logging
5. Strictly prohibit using `mockito` in unit tests, use `mockk` instead
6. Extension methods and pure functions should be separated, not in the same file
  + Extension function file naming convention: use `*Extensions.kt` suffix
  + Pure function file naming convention: use `*Functions.kt` suffix
7. Static functions cannot use "of" but must use "operator fun get" and be decorated with @JvmStatic
8. Logger instance acquisition must be decorated with @JvmStatic

**TypeScript Rules**

- Enable strict mode and all strict type checking options
- Enable strictNullChecks to prevent null/undefined type errors
- Prohibit implicit any types (noImplicitAny)
- Prohibit implicit this (noImplicitThis)
- Prohibit implicit override (noImplicitOverride)
- Prohibit unused local variables (noUnusedLocals)
- Prohibit unused parameters (noUnusedParameters)
- Use unknown type in catch variables (useUnknownInCatchVariables)
- Strictly prohibit using `// @ts-ignore` comments to suppress type errors
- Use Bundler module resolution (moduleResolution: "Bundler")
- Enable verbatim module syntax (verbatimModuleSyntax)
- Enable isolated module compilation (isolatedModules)
- Strictly prohibit mixing JavaScript files (allowJs: false)
- Use latest ES standard (target: "ESNext", module: "ESNext")
- Must generate type declaration files (declaration: true)
- Enable isolated declaration generation (isolatedDeclarations)
- Do not emit output files in development, only perform type checking (noEmit: true)
- Preserve code comments (removeComments: false)
- Enable incremental compilation (incremental: true)
- Skip library file type checking for performance (skipLibCheck: true)
- Prefer type inference, avoid redundant explicit type annotations
- Function parameters and return values must have explicit type definitions
- Prefer interface over type alias, use type for complex types
- Strictly prohibit using any type, use unknown when necessary

**SCSS Rules**

- Prohibit using `@import`, use `@use` instead

**Markdown Rules**

- Optimize for AI readability and attention rather than human consumption
- Must use English only for writing
- Nested lists should use alternating pattern: - + - + with maximum 3 nesting levels
- Keep content concise and direct, avoid unnecessary verbosity
- Minimize use of excessive headings
