# Technology Stack

## Core Technologies

- **Language**: Kotlin 2.2.0 (JDK 24+)
- **Framework**: Spring Boot 3.5.4
- **ORM**: Jimmer 0.9.105 (modern ORM framework)
- **Build Tool**: Gradle 9.x with Kotlin DSL
- **Version Management**: Gradle Version Catalog

## Key Dependencies

- **Database**: PostgreSQL (primary), MySQL 8
- **Migration**: Flyway 11.10.5
- **Connection Pool**: HikariCP
- **Security**: Spring Security 6.5.2, OAuth2
- **Caching**: Caffeine, Redis
- **AI/ML**: LangChain4j 1.2.0, MCP protocol
- **Object Storage**: MinIO, Aliyun OSS, Huawei OBS, Volcengine TOS
- **Payment**: WeChat Pay
- **SMS**: Tencent Cloud SMS
- **Testing**: JUnit 6.0.0-M2, MockK, TestContainers 1.21.3
- **JSON Processing**: Jackson 2.19.2
- **Logging**: SLF4J 2.1.0-alpha1, Logback 1.5.18

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

Dependencies are managed through `gradle/libs.versions.toml` with centralized version management. Current project version: 0.0.26. External projects can import the version catalog:

```kotlin
dependencyResolutionManagement {
  versionCatalogs {
    create("cs") { from("io.github.truenine:composeserver-version-catalog:0.0.26") }
  }
}
```

## Code Quality Tools

- **Spotless**: Code formatting and style enforcement
- **OWASP Dependency Check**: Security vulnerability scanning
- **Dokka**: API documentation generation
