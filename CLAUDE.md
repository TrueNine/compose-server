# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this Repository.

**Technology Stack:** Kotlin 2.2.0, Spring Boot 3.5.4, Spring Framework 6.2.9, Jimmer 0.9.105, Gradle 9.0.0, Java 24, PostgreSQL, Redis, Caffeine, MinIO, LangChain4j.

**Project Features:**

- Modern enterprise-grade Kotlin server-side framework library published to Maven Central Repository
- Modular design with 40+ core modules supporting on-demand integration into existing projects
- Licensed under LGPL 2.1 open source license

## Module Structure and Navigation

**Package Format:** `io.github.truenine.composeserver.{module-name}`

**Core Foundation Modules:**

- `shared/` - Core components, utilities, exception handling, unified responses, pagination, type definitions
- `testtoolkit/` - Testing toolkit with TestContainers integration
- `version-catalog/` - Version catalog management
- `bom/` - Bill of Materials for dependency management
- `gradle-plugin/` - Gradle plugins and conventions
- `docsite/` - Documentation site generation

**Business Capability Modules:**

- `cacheable/` - Multi-level caching (Redis, Caffeine)
- `ai/` - AI services
  - `ai-shared` - AI shared components
  - `ai-langchain4j` - LangChain4j integration
- `pay/` - Payment services
  - `pay-shared` - Payment shared components
  - `pay-wechat` - WeChat Pay V3
- `oss/` - Object storage services
  - `oss-shared` - OSS shared components
  - `oss-minio` - MinIO integration
  - `oss-aliyun-oss` - Alibaba Cloud OSS
  - `oss-huawei-obs` - Huawei Cloud OBS
  - `oss-volcengine-tos` - ByteDance VolcEngine TOS
- `rds/` - Relational database services
  - `rds-shared` - RDS shared components
  - `rds-crud` - CRUD operations
  - `rds-jimmer-ext-postgres` - Jimmer PostgreSQL extensions
  - `rds-flyway-migration-postgresql` - Flyway PostgreSQL migrations
  - `rds-flyway-migration-mysql8` - Flyway MySQL8 migrations
  - `rds-flyway-migration-shared` - Flyway shared migration components

**System Service Modules:**

- `security/` - Security services
  - `security:spring` - Spring Security integration
  - `security:oauth2` - OAuth2 support
  - `security:crypto` - Cryptographic components
- `sms/` - SMS services
  - `sms:shared` - SMS shared components
  - `sms:tencent` - Tencent Cloud SMS
- `surveillance/` - Monitoring services
  - `surveillance:shared` - Surveillance shared components
  - `surveillance:hikvision` - Hikvision integration

**Data Processing Modules:**

- `data/` - Data processing
  - `data:crawler` - Web crawling
  - `data:extract` - Data extraction
- `depend/` - Dependency handling
  - `depend:servlet` - Servlet dependencies
  - `depend:paho` - MQTT Paho client
  - `depend:http-exchange` - Spring 6 WebExchange
  - `depend:jackson` - Jackson processing
  - `depend:springdoc-openapi` - OpenAPI documentation
  - `depend:xxl-job` - XXL-Job integration

**Code Generation Modules:**

- `ksp/` - Kotlin Symbol Processing
  - `ksp:plugin` - KSP plugin
  - `ksp:shared` - KSP shared components
  - `ksp:meta` - Metadata definitions

**Platform SDK Modules:**

- `psdk/` - Platform SDK
  - `psdk:wxpa` - WeChat Public Account SDK

**IDE Integration Modules:**

- `ide/` - IDE integrations
  - `ide:idea-mcp` - IntelliJ IDEA MCP plugin

**Common Paths:**

- Build files: `{module}/build.gradle.kts`
- Source code: `{module}/src/main/kotlin/io/github/truenine/composeserver/{module}/`
- Tests: `{module}/src/test/kotlin/`
- Resources: `{module}/src/main/resources/`

## Build Commands

**Basic Build Operations:**

- `./gradlew build` - Build the project
- `./gradlew clean` - Clean build outputs
- `./gradlew publishToMavenLocal` - Publish to local Maven repository
- `./gradlew check` - Run all tests and checks

**Module-specific Operations:**

- `./gradlew :{module}:check` - Run tests for specific module
- `./gradlew :{module}:build` - Build specific module
- `./gradlew :{module}:publishToMavenLocal` - Publish specific module to local repository

**Code Quality:**

- `./gradlew spotlessApply` - Fix code formatting (must run before commit)
- `./gradlew versionCatalogFormat` - Format `libs.versions.toml` file
- `./gradlew versionCatalogUpdate` - Check for dependency updates

**Performance Optimization:**

- JVM Configuration: `-Xmx4g -XX:MaxMetaspaceSize=1g -XX:+UseG1GC`
- Enable parallel builds, caching, and configuration cache

## Build Conventions and Plugins

**build-logic Convention Plugin System:**

- `buildlogic.jacoco-conventions` - Code coverage conventions
- `buildlogic.java-conventions` - Java conventions
- `buildlogic.javaspring-conventions` - Java Spring conventions
- `buildlogic.kotlin-conventions` - Kotlin conventions
- `buildlogic.kotlinspring-conventions` - Kotlin Spring conventions (primary usage)
- `buildlogic.publish-conventions` - Publishing conventions
- `buildlogic.repositories-conventions` - Repository conventions
- `buildlogic.spotless-conventions` - Code formatting conventions
- `buildlogic.spotless-sql-conventions` - SQL code formatting conventions

## Development Standards

**Dependencies and Build:**

- **Dependency Management:** Gradle Version Catalog (`gradle/libs.versions.toml`) for unified version management
- **Plugin Conventions:** All Kotlin modules use `kotlinspring-conventions`, Java modules use corresponding conventions
- **Code Formatting:** Spotless automated format checking (must run `./gradlew spotlessApply` before commit)
- **Version Publishing:** Published to Maven Central Repository as `io.github.truenine:composeserver-*`

**Testing Standards:**

- Test classes have the same name as the tested class, organized using @Nested
- Disable @DisplayName annotation, use backtick Chinese method names
- TestContainers integration testing supports PostgreSQL/MySQL/Redis/MinIO
- Test organization: Normal cases, exception cases, boundary cases grouped

**Architecture Conventions:**

- Package naming: `io.github.truenine.composeserver.{module-name}`
- Auto-configuration: Spring Boot AutoConfiguration + @ConditionalOn* conditional configuration
- Resource management: ResourceHolder unified management of configuration files and static resources

## Architecture Features

**Modular Design:**

- Each module is independently packaged and published to Maven Central Repository, supporting on-demand integration
- build-logic convention plugins uniformly manage build configuration and code quality standards

**Testing Architecture:**

- TestContainers integration testing: PostgreSQL, MySQL, Redis, MinIO containerized testing
- @Nested inner classes organize test scenarios: normal cases, exception cases, boundary cases
- Test idempotency verification: ensures safety of multiple executions of database migrations and stored procedures

**Auto-configuration System:**

- Spring Boot AutoConfiguration automatically assembles module functionality
- Conditional configuration: Controls component enablement through Properties classes and @ConditionalOn* annotations
- Resource management: ResourceHolder unified management of configuration files and static resource loading

## Development Guide

**Build Environment Requirements:**

- Java 24+
- Kotlin 2.2.0
- Gradle 9.x (using included builds and version catalog management)

**Development Workflow:**

1. Must run `./gradlew spotlessApply` to fix code formatting before commit
2. Ensure all tests pass with `./gradlew check`
3. Use @Nested to organize tests, disable @DisplayName, use backtick Chinese method names
4. New modules need to be declared in `settings.gradle.kts` and apply appropriate build conventions

**Version Management:**

- Dependency versions are unified in `gradle/libs.versions.toml`
- Use `./gradlew versionCatalogUpdate` to check for dependency updates
- Version publishing through Maven Central Repository, naming rule: `io.github.truenine:composeserver-{module-name}`
