# Git Commit Standards

## Commit Message Format

**Basic Format:** `emoji [scope] description`

- **emoji**: Emoji representing the type of change
- **scope**: Affected scope (module name), enclosed in square brackets
- **description**: Concise description of the change

**Complex Change Format:** When a single commit contains multiple changes, use list format

## Emoji Standards

| Emoji | Type        | Description       | Use Cases                    |
|-------|-------------|-------------------|------------------------------|
| 🎉    | feat        | Major feature/init| New features, major updates, project initialization |
| ✨     | feat        | New feature/enhancement | Add features, enhancements, documentation updates |
| 🐛    | fix         | Bug fix           | Fix errors, resolve issues   |
| 🔧    | config      | Configuration changes | Config files, CI/CD, build configuration |
| 📝    | docs        | Documentation updates | Update docs, README, comments |
| 🎨    | style       | Code style/formatting | Code formatting, styling, structure optimization |
| ♻️    | refactor    | Refactoring       | Code refactoring, package structure adjustments |
| ⚡     | perf        | Performance optimization | Performance optimization, algorithm improvements |
| 🔥    | remove      | Remove code/files | Delete unused code, remove features |
| 🧪    | test        | Test-related      | Add tests, fix tests, test configuration |
| 👷    | ci          | CI/CD             | Continuous integration, build scripts |
| 📦    | build       | Build system      | Dependency management, build configuration |
| ⬆️    | upgrade     | Upgrade dependencies | Upgrade library versions     |
| ⬇️    | downgrade   | Downgrade dependencies | Downgrade library versions   |
| 🚀    | release     | Release version   | Version releases, tag creation |
| 🔀    | merge       | Merge branches    | Branch merging, conflict resolution |
| 🤖    | ai          | AI tool configuration | AI assistant configuration, automation |
| 💄    | optimize    | Optimization      | Performance optimization, code improvements |
| 🌐    | network     | Network-related   | Network configuration, API calls, remote services |
| 🔐    | security    | Security/validation | Security fixes, access control, validation |
| 🚑    | hotfix      | Emergency fix     | Emergency fixes, temporary solutions |
| 📈    | analytics   | Analytics/monitoring | Performance monitoring, data analysis |
| 🍱    | assets      | Asset files       | Images, fonts, static resources |
| 🚨    | lint        | Code checking     | Fix linting warnings, code quality |
| 💡    | comment     | Comments          | Add/update comments, docstrings |
| 🔊    | log         | Logging           | Add logs, debug information  |
| 🔇    | log         | Remove logs       | Delete logs, silence output  |

## Commit Examples

### Simple Format Examples

```bash
✨ [shared] Add unified exception handling

🐛 [rds] Fix connection pool configuration issue

♻️ [security] Refactor JWT validation logic
```

### Complex Format Examples

```bash
✨ [ai] LangChain4j integration optimization

- 🚑 Fix model loading timeout issue
- 🐛 Resolve dependency conflict issues
- 💄 Optimize AI service performance
- 🧪 Add integration test cases
```

## Commit Standards Requirements

1. **Must use emojis**: Each commit message must start with the corresponding emoji
2. **Clear scope**: Use square brackets to clearly identify affected modules or components
3. **Concise description**: Start with a verb, concisely describe the change content
4. **Single responsibility**: Each commit should focus on a single type of change
5. **English description**: Commit descriptions use English for better understanding
