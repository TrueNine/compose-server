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
└── docs/                 # Project documentation
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
- `rds-flyway-migration-*/` - Database migration modules

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
- `ksp/` - Kotlin Symbol Processing plugins
- `psdk/` - Platform SDK integrations

## Module Naming Convention

- **Prefix**: All published artifacts use `composeserver-` prefix
- **Group ID**: `io.github.truenine`
- **Structure**: `{domain}-{implementation}` (e.g., `oss-minio`, `security-oauth2`)

## Key Architectural Principles

1. **Shared Foundation**: All modules depend on `shared/` for common utilities
2. **Clean Boundaries**: Each module has clear responsibilities and interfaces
3. **Testability**: Comprehensive test coverage with `testtoolkit/` support
4. **Modularity**: Consumers can pick and choose specific modules
5. **Convention over Configuration**: Consistent patterns across all modules
