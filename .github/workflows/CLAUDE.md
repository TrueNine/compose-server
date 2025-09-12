# GitHub Actions Workflows - CLAUDE.md

This file provides configuration and usage guidance for GitHub Actions workflows for Claude Code.

## Actions Versions and Dependencies

### Core Actions and Versions

**Code Management Actions:**
- `actions/checkout@v5.0.0` - Repository checkout with support for `fetch-depth: 0` full history
- `actions/setup-java@v5.0.0` - Java/JDK environment configuration using `temurin` distribution
- `gradle/actions/setup-gradle@v4` - Gradle build tool configuration with caching strategy support

**Cache Management Actions:**
- `actions/cache@v4` - General cache management supporting Gradle dependencies and build output caching
- `actions/cache/restore@v4` - Cache restore only, for read-only cache scenarios

**Publishing and Deployment Actions:**
- `softprops/action-gh-release@v2` - GitHub Release creation with automatic release notes generation
- `nick-fields/retry@v3` - Operation retry mechanism for unstable network publishing scenarios

**Test Report Actions:**
- `actions/upload-artifact@v4` - Test results upload with 5-day retention period
- `actions/download-artifact@v5.0.0` - Test results download and aggregation

## Workflow Files and Functions

### 1. `.github/workflows/ci.yaml` - Continuous Integration Workflow

**Trigger Conditions:**
- Pull Requests targeting the `main` branch
- Path filtering: Changes to `.github/workflows/ci.yaml` and `**/*.kt` files

**Main Functions:**
- **Quick Check Stage (quick-check):** Version extraction, environment configuration, quick compilation
- **Matrix Testing (test-matrix):** Grouped parallel testing supporting 12 module groups
- **Test Results Summary (test-results):** Generate test report summary

**Module Grouping Strategy:**
- `core-foundation`: Core foundation modules (shared, cacheable, docsite)
- `core-build-tools`: Build tool modules (gradle-plugin, version-catalog, bom)
- `rds-*`: Database-related modules, divided into lightweight and heavyweight tests
- `business-*`: Business function modules (AI, payment, object storage, communication)
- `security`: Security modules
- `data-processing`: Data processing modules
- `platform-integrations`: Platform integration modules
- `testing-tools`: Testing tool modules
- `integration-tests`: Integration test modules

**TestContainers Support:**
- Heavy module groups enable TestContainers (PostgreSQL, MySQL, Redis, MinIO)
- Container reuse configuration: `TESTCONTAINERS_REUSE_ENABLE: true`
- Automatic cleanup of non-reused containers

### 2. `.github/workflows/maven-central-publish.yaml` - Maven Central Publishing Workflow

**Trigger Conditions:**
- Push to `main` branch
- Version tag push (`v*`)
- Release publication events
- Manual trigger (workflow_dispatch)

**Workflow Stages:**

1. **Pre-publish Validation (pre-publish-validation):**
  - Version format validation (following semantic versioning)
  - Maven Central version existence check
  - Build environment configuration validation

2. **Publish to Maven Central (publish):**
  - GPG signature configuration
  - Dry run support (dry_run parameter)
  - Force publish support (force_publish parameter)
  - Retry mechanism (maximum 2 times, 60-second intervals)

3. **Create GitHub Release (create-github-release):**
  - Automatic release notes generation
  - Include Maven Central usage examples
  - Create releases only for stable versions

4. **Post-publish Verification (post-publish-verification):**
  - Publishing status confirmation
  - Generate verification reports

5. **Failure Handling (failure-handler):**
  - Failure cause analysis
  - Troubleshooting recommendations

**Key Configuration:**
- Environment variables: `GRADLE_OPTS` for build performance optimization
- Security credentials: GPG signing, Maven Central authentication
- Concurrency control: Prevent parallel publishing of the same branch

## Environment Configuration Requirements

**Runtime Environment:**
- Ubuntu Latest (GitHub-hosted runners)
- Java 17+ (automatically extracted from `libs.versions.toml`)
- Gradle 9.1+ (automatically extracted from `libs.versions.toml`)
- Docker (TestContainers support)

**External Dependencies:**
- Maven Central Repository (publishing target)
- Sonatype OSSRH (publishing intermediary)
- Docker Hub (container images)

**Required GitHub Secrets:**
- `GPG_KEY_ID`: GPG key ID
- `GPG_PRIVATE_KEY`: GPG private key
- `GPG_PASSPHRASE`: GPG key passphrase
- `MAVENCENTRAL_USERNAME`: Maven Central username
- `MAVENCENTRAL_PASSWORD`: Maven Central password
