---
inclusion: always
---

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
