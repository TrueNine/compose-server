# 🚀 Compose Server MCP Plugin

## IDEA testing guide

- **[Test Overview](https://plugins.jetbrains.com/docs/intellij/testing-plugins.html)** – overview of plugin testing
- **[Tests and Fixtures](https://plugins.jetbrains.com/docs/intellij/testing-plugins.html#tests-and-fixtures)** – tests and fixtures
- **[Light and Heavy Tests](https://plugins.jetbrains.com/docs/intellij/light-and-heavy-tests.html)** – light-weight and heavy-weight tests
- **[Test Project and Testdata Directories](https://plugins.jetbrains.com/docs/intellij/test-project-and-testdata-directories.html)** – test project and testdata directories
- **[Running Tests](https://plugins.jetbrains.com/docs/intellij/writing-tests.html)** – writing and running tests
- **[Highlighting Tests](https://plugins.jetbrains.com/docs/intellij/testing-highlighting.html)** – highlighting tests
- **[Testing FAQ](https://plugins.jetbrains.com/docs/intellij/testing-faq.html)** – frequently asked questions
- **[Integration Tests](https://plugins.jetbrains.com/docs/intellij/integration-test.html)** – integration tests
  + **[Introduction to Integration Tests](https://plugins.jetbrains.com/docs/intellij/integration-tests-intro.html)** – introduction
  + **[UI Testing](https://plugins.jetbrains.com/docs/intellij/integration-tests-ui.html)** – UI testing
  + **[API Testing](https://plugins.jetbrains.com/docs/intellij/integration-tests-api.html)** – API testing

## 📖 IDEA plugin development docs

- **[IntelliJ Platform Plugin SDK](https://plugins.jetbrains.com/docs/intellij/welcome.html)** – official plugin development docs
- **[Plugin Development Guidelines](https://plugins.jetbrains.com/docs/intellij/plugin-development-guidelines.html)** – plugin development guidelines
- **[IntelliJ Platform UI Guidelines](https://plugins.jetbrains.com/docs/intellij/ui-guidelines.html)** – UI design guidelines
- **[Plugin Publishing](https://plugins.jetbrains.com/docs/intellij/publishing-plugin.html)** – plugin publishing guide

## 🛠️ Tools overview

### 1. TerminalTool

- **Function**: execute terminal commands and return cleaned output
- **Parameters**:
  - `command`: command to execute
  - `workingDirectory`: working directory
  - `timeout`: timeout in milliseconds
  - `cleanOutput`: whether to clean the output

### 2. ViewErrorTool

- **Function**: view errors for a file or directory
- **Parameters**:
  - `path`: file or directory path
  - `includeWarnings`: whether to include warnings
  - `includeWeakWarnings`: whether to include weak warnings

### 3. CleanCodeTool

- **Function**: clean and format code
- **Parameters**:
  - `path`: file path
  - `formatCode`: whether to format code
  - `optimizeImports`: whether to optimize imports
  - `runInspections`: whether to run inspections

### 4. ViewLibCodeTool

- **Function**: view third-party library source code
- **Parameters**:
  - `filePath`: file path
  - `fullyQualifiedName`: fully-qualified class name
  - `memberName`: member method name
