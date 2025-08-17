# Project Structure

## Root Level Organization

```
compose-server/
├── shared/                 # Core foundation library
├── bom/                   # Bill of Materials for dependency management
├── version-catalog/       # Gradle version catalog
├── testtoolkit/          # Testing utilities and helpers
├── gradle-plugin/        # Custom Gradle plugins
├── cacheable/            # Caching abstractions
├── build-logic/          # Build convention plugins
├── docs/                 # Project documentation
├── docsite/              # Documentation site generation
├── integrate-test/       # Integration test modules
├── ide/                  # IDE plugins and extensions
└── .kiro/                # Kiro AI assistant configuration
```

## Domain Modules

### AI Services (`ai/`)
- `ai-shared/` - Common AI abstractions
- `ai-langchain4j/` - LangChain4j integration

### Object Storage (`oss/`)
- `oss-shared/` - Common storage interfaces
- `oss-minio/` - MinIO implementation
- `oss-aliyun-oss/` - Aliyun OSS implementation
- `oss-huawei-obs/` - Huawei OBS implementation
- `oss-volcengine-tos/` - Volcengine TOS implementation

### Database (`rds/`)
- `rds-shared/` - Common database abstractions
- `rds-crud/` - CRUD operations
- `rds-jimmer-ext-postgres/` - PostgreSQL Jimmer extensions
- `rds-flyway-migration-shared/` - Common migration utilities
- `rds-flyway-migration-mysql8/` - MySQL 8 migration support
- `rds-flyway-migration-postgresql/` - PostgreSQL migration support

### Security (`security/`)
- `security-spring/` - Spring Security integration
- `security-oauth2/` - OAuth2 implementation
- `security-crypto/` - Cryptographic utilities

### Payment (`pay/`)
- `pay-shared/` - Payment abstractions
- `pay-wechat/` - WeChat Pay integration

### SMS (`sms/`)
- `sms-shared/` - SMS abstractions
- `sms-tencent/` - Tencent Cloud SMS

### Data Processing (`data/`)
- `data-crawler/` - Web crawling utilities
- `data-extract/` - Data extraction tools

### Surveillance (`surveillance/`)
- `surveillance-shared/` - Common surveillance interfaces
- `surveillance-hikvision/` - Hikvision integration

## Build System Structure

### Convention Plugins (`build-logic/`)
- Centralized build logic and conventions
- Shared configuration for all modules
- Plugin composition for consistent builds

### Dependency Management
- `depend/` - Third-party dependency wrappers
  - `depend-jackson/` - Jackson JSON processing
  - `depend-http-exchange/` - HTTP client abstractions
  - `depend-paho/` - MQTT client integration
  - `depend-servlet/` - Servlet API wrappers
  - `depend-springdoc-openapi/` - OpenAPI documentation
  - `depend-xxl-job/` - XXL-Job distributed scheduler
- `ksp/` - Kotlin Symbol Processing plugins
  - `ksp-meta/` - Metadata processing
  - `ksp-plugin/` - KSP plugin implementation
  - `ksp-shared/` - Common KSP utilities
- `psdk/` - Platform SDK integrations
  - `psdk-wxpa/` - WeChat Public Account SDK

## Module Naming Convention

- **Prefix**: All published artifacts use `composeserver-` prefix
- **Group ID**: `io.github.truenine`
- **Structure**: `{domain}-{implementation}` (e.g., `oss-minio`, `security-oauth2`)

## IDE Integration (`ide/`)
- `ide-idea-mcp/` - IntelliJ IDEA MCP (Model Context Protocol) plugin

## Integration Testing (`integrate-test/`)
- `integrate-test/depend/jackson/` - `depend/jackson` Module Jackson dependency integration tests

### Integration Test Package Naming Convention
- **Domain Modules**: `itest.integrate.{domain}.{moduleName}`
  - `itest.integrate.oss.ossminio` - MinIO storage integration tests
  - `itest.integrate.depend.dependjackson` - Jackson dependency integration tests
  - `itest.integrate.security.securityoauth2` - OAuth2 security integration tests
- **Root Level Modules**: `itest.integrate.{moduleName}`
  - `itest.integrate.shared` - Shared foundation library integration tests
  - `itest.integrate.cacheable` - Caching abstractions integration tests
- **Framework Variants**: Additional framework-specific suffixes are supported
  - `itest.integrate.depend.dependjackson.quarkus` - Jackson with Quarkus framework tests

## Key Architectural Principles

1. **Shared Foundation**: All modules depend on `shared/` for common utilities
2. **Clean Boundaries**: Each module has clear responsibilities and interfaces
3. **Testability**: Comprehensive test coverage with `testtoolkit/` support
4. **Modularity**: Consumers can pick and choose specific modules
5. **Convention over Configuration**: Consistent patterns across all modules
6. **AI-Assisted Development**: Kiro AI integration for enhanced development workflow
7. **IDE Integration**: Native IDE support through plugins and extensions
