# Technology Stack

## Core Technologies

- **Language**: Kotlin 2.2.0 (JDK 24+)
- **Framework**: Spring Boot 3.5.3
- **ORM**: Jimmer 0.9.102 (modern ORM framework)
- **Build Tool**: Gradle 9.x with Kotlin DSL
- **Version Management**: Gradle Version Catalog

## Key Dependencies

- **Database**: PostgreSQL (primary)
- **Migration**: Flyway Migration
- **Connection Pool**: HikariCP
- **Security**: Spring Security, OAuth2
- **Caching**: Caffeine, Redis
- **AI/ML**: LangChain4j, MCP protocol
- **Object Storage**: MinIO, Aliyun OSS, Huawei OBS, Volcengine TOS
- **Payment**: WeChat Pay
- **SMS**: Tencent Cloud SMS
- **Testing**: JUnit 5, MockK, TestContainers

## Build System

### Common Commands

```bash
# Build entire project
./gradlew build

# Run tests
./gradlew test

# Publish to local repository
./gradlew publishToMavenLocal

# Check for dependency updates
./gradlew versionCatalogUpdate

# Security vulnerability scan
./gradlew dependencyCheckAnalyze

# Code formatting
./gradlew spotlessApply
```

### Gradle Configuration

- **Parallel builds**: Enabled
- **Build cache**: Enabled
- **Configuration cache**: Enabled
- **JVM args**: 4GB heap, G1GC
- **Kotlin incremental compilation**: Enabled

### Version Catalog

Dependencies are managed through `gradle/libs.versions.toml` with centralized version management. External projects can import the version catalog:

```kotlin
dependencyResolutionManagement {
  versionCatalogs {
    create("cs") { from("io.github.truenine:composeserver-version-catalog:latest") }
  }
}
```

## Code Quality Tools

- **Spotless**: Code formatting and style enforcement
- **OWASP Dependency Check**: Security vulnerability scanning
- **Dokka**: API documentation generation
