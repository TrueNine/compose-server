# CODEBUDDY.md

This file provides guidance to CodeBuddy Code when working with the Compose Server repository.

## Project Overview

**Compose Server** is a modern enterprise-grade Kotlin server-side framework library featuring modular architecture design. It provides 30+ specialized modules covering AI integration, object storage, database operations, security, payments, messaging, data processing, and monitoring capabilities. All modules are published to Maven Central for selective integration.

**Technology Stack:** Kotlin 2.2.0, Spring Boot 3.5.4, Jimmer 0.9.105, Gradle 9.x, Java 24+, PostgreSQL, Redis, Caffeine, MinIO, LangChain4j

## Essential Development Commands

### Build and Development
```bash
# Build the entire project
./gradlew build

# Clean build outputs
./gradlew clean

# Run all tests and checks
./gradlew check

# Build specific module
./gradlew :{module}:build

# Run tests for specific module
./gradlew :{module}:check

# Publish to local Maven repository
./gradlew publishToMavenLocal

# Publish specific module locally
./gradlew :{module}:publishToMavenLocal
```

### Code Quality (REQUIRED before commits)
```bash
# Fix code formatting (MUST run before commit)
./gradlew spotlessApply

# Format version catalog
./gradlew versionCatalogFormat

# Check for dependency updates
./gradlew versionCatalogUpdate
```

### Testing Commands
```bash
# Run all tests with parallel execution
./gradlew test

# Run tests with TestContainers (requires Docker)
./gradlew integrationTest

# Run specific test class
./gradlew test --tests "ClassName"

# Run tests with specific profile
./gradlew test -Dspring.profiles.active=test
```

## Architecture Overview

### Modular Structure
The project follows a layered modular architecture with clear functional boundaries:

```
🎯 Application Layer
    ↓ (selective integration)
🏢 Business Module Layer (30+ modules)
    ├── 🤖 AI Services (ai-*)
    ├── 📦 Object Storage (oss-*)
    ├── 💳 Payment Services (pay-*)
    ├── 📱 SMS Services (sms-*)
    ├── 🔐 Security (security-*)
    ├── 🗄️ Database (rds-*)
    └── 🔧 Dependencies (depend-*, ksp-*, psdk-*)
    ↓ (all depend on)
🏗️ Infrastructure Layer
    ├── 🔧 Shared Foundation (shared)
    ├── 📋 Dependency Management (bom)
    ├── 🧪 Testing Toolkit (testtoolkit)
    └── ⚡ Caching Abstractions (cacheable)
```

### Module Organization Pattern
Each functional domain follows a consistent structure:
- `{domain}-shared/`: Core interfaces and abstractions
- `{domain}-{provider}/`: Provider-specific implementations (e.g., `oss-minio`, `pay-wechat`)
- `autoconfig/`: Spring Boot auto-configuration classes
- Package format: `io.github.truenine.composeserver.{module-name}`

### Key Architectural Patterns

#### Auto-Configuration Pattern
Every module uses Spring Boot auto-configuration with:
- `AutoConfigEntrance.kt` as entry point in `autoconfig` package
- `@ComponentScan` for automatic discovery
- `@EnableConfigurationProperties` for type-safe configuration
- `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` for registration

#### Repository Pattern
- `IRepo` interface extending Jimmer's `KRepository`
- `IPersistentEntity` base interface for all entities
- Jimmer ORM integration with strong type safety

#### Service Abstractions
- Unified interfaces like `IObjectStorageService` for multi-provider support
- Coroutines integration throughout async operations
- Provider-swappable implementations

## Testing Architecture

### TestContainers Integration
The project uses comprehensive TestContainers integration:
- `ITestContainerBase`: Base interface for all containers
- Specific container interfaces: `IDatabasePostgresqlContainer`, `ICacheRedisContainer`, etc.
- Automatic container startup via `@DynamicPropertySource`
- Container reuse enabled for performance
- Extension functions for convenient testing (e.g., `mysql { }` blocks)

### Test Organization Standards
- Test classes use `@Nested` inner classes for scenario organization
- Chinese method names with backticks for test descriptions
- Disable `@DisplayName` annotation
- Test categories: normal cases, exception cases, boundary cases
- Idempotency verification for database operations

### Test Configuration
```yaml
# application-test.yml
compose:
  testtoolkit:
    enabled: true
    disable-condition-evaluation-report: true
    enable-virtual-threads: true
    ansi-output-mode: always
```

## Build System

### Gradle Configuration
- **Version Catalog**: `gradle/libs.versions.toml` for unified dependency management
- **Convention Plugins**: `build-logic/` contains reusable build conventions
- **Primary Convention**: `kotlinspring-conventions` for most modules
- **Performance Optimized**: Parallel builds, caching, configuration cache enabled

### Convention Plugins Available
- `buildlogic.kotlinspring-conventions`: Primary for Kotlin Spring modules
- `buildlogic.publish-conventions`: Maven Central publishing
- `buildlogic.spotless-conventions`: Code formatting
- `buildlogic.jacoco-conventions`: Code coverage

### Key Build Files
- Root: `build.gradle.kts`, `settings.gradle.kts`, `gradle.properties`
- Module: `{module}/build.gradle.kts`
- Version management: `gradle/libs.versions.toml`
- Build logic: `build-logic/src/main/kotlin/`

## Development Workflow

### Before Every Commit
1. **MANDATORY**: Run `./gradlew spotlessApply` to fix code formatting
2. Run `./gradlew check` to ensure all tests pass
3. Verify module builds independently if changes span multiple modules

### Adding New Modules
1. Create module directory following naming convention
2. Add to `settings.gradle.kts` include list
3. Apply appropriate convention plugins in module's `build.gradle.kts`
4. Implement auto-configuration in `autoconfig` package
5. Add to version catalog if publishing to Maven Central

### Code Generation (KSP)
- Annotation definitions in `ksp-meta/`
- Processor implementation in `ksp-plugin/`
- Generated code integration through Gradle KSP plugin

## Key Interfaces and Abstractions

### Core Domain Interfaces
- `IAnyEnum`: Type-safe enum abstraction for serialization
- `IPageParam`: Pagination parameter interface
- `IPersistentEntity`: Base entity with audit fields

### Service Layer Interfaces
- `IObjectStorageService`: Unified object storage with coroutines
- `IRepo`: Repository pattern with Jimmer integration
- `IKeysRepo`: Cryptographic key management

### Configuration Interfaces
- Properties classes with `@ConfigurationProperties`
- Environment-specific configuration overrides
- TestContainer-specific properties

## Maven Central Publishing

All modules are published as `io.github.truenine:composeserver-{module-name}:${version}`
- Current version managed in `gradle/libs.versions.toml`
- BOM available for unified version management
- Selective integration supported - choose only needed modules

## Performance Considerations

### JVM Configuration
```properties
# gradle.properties
org.gradle.jvmargs=-Xmx5g -XX:MaxMetaspaceSize=1g -XX:+UseG1GC
kotlin.daemon.jvmargs=-Xmx2g -XX:+UseG1GC
```

### Build Optimizations
- Parallel builds enabled
- Gradle build cache enabled
- Configuration cache enabled
- Incremental Kotlin compilation
- JUnit parallel test execution

## IDE Integration

### IntelliJ IDEA Plugin
- MCP (Model Context Protocol) integration in `ide/ide-idea-mcp/`
- Code analysis and cleanup tools
- Terminal integration for development workflows

### Development Setup
- Java 24+ required
- Kotlin 2.2.0+ required
- Docker required for TestContainers integration
- IntelliJ IDEA recommended with Kotlin plugin