# Compose Server

<div align="center">

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-6DB33F?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![Jimmer](https://img.shields.io/badge/Jimmer-0.9.105-FF6B35?style=for-the-badge)](https://github.com/babyfish-ct/jimmer)
[![JDK](https://img.shields.io/badge/JDK-24+-orange?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)

[![Maven Central](https://img.shields.io/maven-central/v/io.github.truenine/composeserver-shared?style=for-the-badge&logo=apache-maven&logoColor=white&label=Maven%20Central)](https://central.sonatype.com/search?q=g:io.github.truenine)
[![License](https://img.shields.io/badge/License-LGPL%202.1-blue?style=for-the-badge&logo=gnu&logoColor=white)](LICENSE)

[![GitHub Stars](https://img.shields.io/github/stars/TrueNine/compose-server?style=for-the-badge&logo=github&logoColor=white)](https://github.com/TrueNine/compose-server/stargazers)
[![GitHub Issues](https://img.shields.io/github/issues/TrueNine/compose-server?style=for-the-badge&logo=github&logoColor=white)](https://github.com/TrueNine/compose-server/issues)

</div>

---

<div align="center">

## 🚀 Modern Enterprise Kotlin Server Framework

</div>

> **Modular • Enterprise-Grade • Production-Ready**  
> A comprehensive modular server-side framework built on modern technology stack

**Compose Server** is a modern Kotlin enterprise server-side framework library featuring modular architecture design. It provides **30+ specialized modules** covering AI integration, object storage, database operations, security, payments, messaging, data processing, and monitoring capabilities. All modules are published to **Maven Central** for selective integration, enabling developers to build production-ready enterprise applications rapidly.

### 🎯 Target Audience

- **Enterprise Application Developers** - Building reliable, scalable server-side applications
- **Microservices Architecture Teams** - Seeking unified technology stack and modular solutions
- **AI Application Developers** - Integrating multiple AI capabilities into modern applications
- **Multi-Cloud Storage Users** - Managing different cloud storage services with unified interfaces

### 💼 Core Use Cases

- **Enterprise Web Applications** - E-commerce platforms, management systems, business middleware
- **Microservices Architecture** - Distributed systems, cloud-native applications
- **AI-Driven Applications** - Intelligent chatbots, content generation, data analysis
- **Multi-Cloud Storage Systems** - File management, media processing, data backup

---

## ✨ Key Features

### 🏗️ **Modular Architecture**
- **30+ Business Modules** - Clear functional boundaries and dependencies
- **Selective Integration** - Choose only needed modules, avoid redundant dependencies
- **Unified Interfaces** - Consistent API design, reduced learning curve

### 🎯 **Modern Technology Stack**
- **Kotlin 2.2.0** - Modern JVM language, concise and efficient
- **Spring Boot 3.5.4** - Enterprise framework, production-ready
- **Jimmer 0.9.105** - Modern ORM with strong type safety
- **JDK 24+** - Latest Java features and performance optimizations

### 🤖 **AI Capabilities**
- **LangChain4j** - Complete AI application development framework
- **MCP Protocol** - Model Context Protocol support
- **Multi-Model Support** - Compatible with mainstream AI service providers

### 📦 **Multi-Cloud Storage**
- **Unified Interface** - Single API for multiple cloud providers
- **MinIO** - Private cloud object storage solution
- **Public Cloud Support** - Aliyun OSS, Huawei OBS, Volcengine TOS

### 🔐 **Enterprise Security**
- **Spring Security** - Complete security framework integration
- **OAuth2** - Standardized authentication and authorization
- **Crypto Tools** - Common encryption algorithms encapsulation

### 💳 **Payment Integration**
- **WeChat Pay** - Complete payment process encapsulation
- **Unified Interface** - Easy to extend other payment methods

### 📱 **Messaging & Notifications**
- **SMS Service** - Tencent Cloud SMS integration
- **Extensible** - Support for multiple notification channels

### 🗄️ **Data Processing**
- **CRUD Operations** - Jimmer-based data access layer
- **Database Migration** - Flyway integration with version management
- **PostgreSQL Extensions** - Optimizations for PostgreSQL

---

## 🛠️ Technology Stack

### 🏗️ Core Framework

| Technology | Version | Selection Reason |
|------------|---------|------------------|
| **[Kotlin](https://kotlinlang.org/)** | 2.2.0 | Modern JVM language, concise and efficient, fully compatible with Java ecosystem |
| **[Spring Boot](https://spring.io/projects/spring-boot)** | 3.5.4 | Enterprise framework, production-ready, rich ecosystem |
| **[Jimmer](https://github.com/babyfish-ct/jimmer)** | 0.9.105 | Modern ORM framework, strong type safety, GraphQL-style queries |
| **[Gradle](https://gradle.org/)** | 9.x | Powerful build tool, supports Kotlin DSL and Version Catalog |

### 🗄️ Database & Persistence

| Technology | Version | Use Case |
|------------|---------|----------|
| **[PostgreSQL](https://www.postgresql.org/)** | 42.7.7 | Primary database, world's most advanced open source relational database |
| **[Flyway](https://flywaydb.org/)** | 11.10.5 | Database version management and migration tool |
| **[HikariCP](https://github.com/brettwooldridge/HikariCP)** | - | High-performance JDBC connection pool, Spring Boot default |
| **[Caffeine](https://github.com/ben-manes/caffeine)** | 3.2.2 | High-performance local cache library, Guava Cache replacement |

### 🤖 AI & Machine Learning

| Technology | Version | Features |
|------------|---------|----------|
| **[LangChain4j](https://github.com/langchain4j/langchain4j)** | 1.2.0 | AI application development framework for Java, supports multiple LLMs |
| **[LangChain4j Community](https://github.com/langchain4j/langchain4j)** | 1.1.0-beta7 | Community extensions with more AI service integrations |
| **[MCP Protocol](https://modelcontextprotocol.io/)** | 1.1.0-beta7 | Model Context Protocol, unified AI service interface |

### 📦 Object Storage

| Provider | SDK Version | Use Case |
|----------|-------------|----------|
| **[MinIO](https://min.io/)** | 8.5.17 | Private cloud storage, S3-compatible, suitable for self-hosted environments |
| **[Aliyun OSS](https://www.aliyun.com/product/oss)** | 3.18.3 | Public cloud storage, fast access in China |
| **[Huawei OBS](https://www.huaweicloud.com/product/obs.html)** | 3.25.5 | Public cloud storage, enterprise-grade reliability |
| **[Volcengine TOS](https://www.volcengine.com/products/tos)** | 2.9.4 | Public cloud storage, ByteDance cloud service |

### 🔐 Security & Authentication

| Technology | Version | Application |
|------------|---------|-------------|
| **[Spring Security](https://spring.io/projects/spring-security)** | 6.5.2 | Enterprise security framework, authentication and authorization core |
| **[OAuth2](https://oauth.net/2/)** | - | Standardized authentication and authorization protocol |
| **[JWT](https://jwt.io/)** | 4.5.0 | Stateless tokens, suitable for distributed systems |
| **[BouncyCastle](https://www.bouncycastle.org/)** | 1.81 | Cryptographic library, rich cryptographic functions |

### 💳 Payment & Communication

| Service | SDK Version | Integration |
|---------|-------------|-------------|
| **[WeChat Pay](https://pay.weixin.qq.com/)** | 0.2.17 | Complete payment process encapsulation, multiple payment methods |
| **[Tencent Cloud SMS](https://cloud.tencent.com/product/sms)** | 3.1.1281 | SMS service, verification codes and notification messages |

### 🔧 Development Tools & Testing

| Tool | Version | Purpose |
|------|---------|---------|
| **[JUnit 5](https://junit.org/junit5/)** | 6.0.0-M2 | Modern Java testing framework |
| **[MockK](https://mockk.io/)** | 1.14.5 | Kotlin-specific mocking framework |
| **[TestContainers](https://www.testcontainers.org/)** | 1.21.3 | Integration testing containerization solution |
| **[Spotless](https://github.com/diffplug/spotless)** | 7.2.1 | Code formatting and style checking tool |

---

## 🏗️ Module Architecture

### 📊 Architecture Overview

Compose Server adopts a layered modular architecture providing clear functional boundaries and flexible integration:

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
    ├── 📊 Data Processing (data-*)
    ├── 📹 Surveillance (surveillance-*)
    └── 🔧 Dependencies (depend-*, ksp-*, psdk-*)
    ↓ (all depend on)
🏗️ Infrastructure Layer
    ├── 🔧 Shared Foundation (shared)
    ├── 📋 Dependency Management (bom)
    ├── 🧪 Testing Toolkit (testtoolkit)
    ├── ⚡ Caching Abstractions (cacheable)
    └── 📚 Version Catalog (version-catalog)
```

### 🎨 Module Categories

#### 🏛️ **Infrastructure Modules**

| Module | Description | Use Case |
|--------|-------------|----------|
| **shared** | Core foundation library with common utilities and abstractions | Base dependency for all business modules |
| **bom** | Bill of Materials for unified version management | Project dependency and version control |
| **testtoolkit** | Testing toolkit with infrastructure and utilities | Unit testing, integration testing, performance testing |
| **cacheable** | Caching abstraction layer with unified interfaces | Application cache, data cache, session cache |
| **version-catalog** | Gradle version catalog for external projects | External project integration and version sync |

#### 🤖 **AI Service Modules**

| Module | Description | Use Case |
|--------|-------------|----------|
| **ai-shared** | Common AI service abstractions and interfaces | Unified interface layer for AI capabilities |
| **ai-langchain4j** | LangChain4j integration supporting multiple LLMs | Intelligent dialogue, content generation, document analysis |

#### 📦 **Object Storage Modules**

| Module | Description | Use Case |
|--------|-------------|----------|
| **oss-shared** | Unified object storage interfaces and abstractions | Unified access layer for storage services |
| **oss-minio** | MinIO private cloud storage implementation | Self-hosted storage, development and testing environments |
| **oss-aliyun-oss** | Aliyun OSS integration | Domestic public cloud storage service |
| **oss-huawei-obs** | Huawei OBS integration | Enterprise-grade public cloud storage |
| **oss-volcengine-tos** | Volcengine TOS integration | ByteDance cloud storage service |

#### 🗄️ **Database Modules**

| Module | Description | Use Case |
|--------|-------------|----------|
| **rds-shared** | Common database abstractions and utilities | Data access layer infrastructure |
| **rds-crud** | Jimmer-based CRUD operations encapsulation | Standard database CRUD operations |
| **rds-jimmer-ext-postgres** | PostgreSQL Jimmer extensions | PostgreSQL-specific feature support |
| **rds-flyway-migration-shared** | Common Flyway migration utilities | Database version management foundation |
| **rds-flyway-migration-mysql8** | MySQL 8.x migration support | MySQL database migration |
| **rds-flyway-migration-postgresql** | PostgreSQL migration support | PostgreSQL database migration |

#### 🔐 **Security Modules**

| Module | Description | Use Case |
|--------|-------------|----------|
| **security-spring** | Spring Security integration and configuration | Web application security, API authentication |
| **security-oauth2** | OAuth2 authentication and authorization | Third-party login, API authorization |
| **security-crypto** | Encryption and decryption utilities | Data encryption, password processing |

#### 💳 **Payment Modules**

| Module | Description | Use Case |
|--------|-------------|----------|
| **pay-shared** | Common payment service abstractions | Unified payment interface definitions |
| **pay-wechat** | WeChat Pay integration | WeChat payments, mini-program payments |

#### 📱 **Communication Modules**

| Module | Description | Use Case |
|--------|-------------|----------|
| **sms-shared** | Common SMS service abstractions | Unified SMS interface |
| **sms-tencent** | Tencent Cloud SMS integration | Verification codes, notification SMS |

#### 📊 **Data Processing Modules**

| Module | Description | Use Case |
|--------|-------------|----------|
| **data-crawler** | Web crawling and data scraping | Data collection, content scraping |
| **data-extract** | Data extraction and transformation tools | Data cleaning, format conversion |

#### 📹 **Surveillance Modules**

| Module | Description | Use Case |
|--------|-------------|----------|
| **surveillance-shared** | Common surveillance service abstractions | Unified surveillance interface definitions |
| **surveillance-hikvision** | Hikvision device integration | Video surveillance, device management |

#### 🔧 **Dependency & Platform Modules**

| Module | Description | Use Case |
|--------|-------------|----------|
| **depend-jackson** | Jackson JSON processing wrapper | JSON serialization optimization |
| **depend-servlet** | Servlet API wrapper | Web application development |
| **depend-springdoc-openapi** | SpringDoc OpenAPI wrapper | API documentation generation |
| **ksp-plugin** | Kotlin Symbol Processing plugin | Code generation and processing |
| **psdk-wxpa** | WeChat Public Account SDK | WeChat ecosystem integration |

---

## 🚀 Quick Start

### 📋 Requirements

| Environment | Minimum | Recommended | Notes |
|-------------|---------|-------------|-------|
| **JDK** | 24+ | 24+ | Latest Java features and performance optimizations |
| **Kotlin** | 2.2.0+ | 2.2.0+ | Modern JVM language, fully Java compatible |
| **Gradle** | 9.0+ | 9.x | Supports Kotlin DSL and Version Catalog |
| **Spring Boot** | 3.5.0+ | 3.5.4+ | Enterprise framework, production-ready |

### 📦 Maven Central

All modules published to Maven Central: [**io.github.truenine**](https://central.sonatype.com/search?q=g:io.github.truenine)

Current version: **0.0.26**

### 🔧 Basic Setup

#### Gradle Kotlin DSL (Recommended)

```kotlin
// build.gradle.kts
plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.spring") version "2.2.0"
    id("org.springframework.boot") version "3.5.4"
    id("io.spring.dependency-management") version "1.1.7"
}

repositories {
    mavenCentral()
}

dependencies {
    // Core modules (required)
    implementation("io.github.truenine:composeserver-shared:0.0.26")
    
    // Database modules
    implementation("io.github.truenine:composeserver-rds-shared:0.0.26")
    implementation("io.github.truenine:composeserver-rds-crud:0.0.26")
    
    // Optional modules (choose as needed)
    implementation("io.github.truenine:composeserver-ai-shared:0.0.26")
    implementation("io.github.truenine:composeserver-ai-langchain4j:0.0.26")
    implementation("io.github.truenine:composeserver-oss-shared:0.0.26")
    implementation("io.github.truenine:composeserver-oss-minio:0.0.26")
    
    // Spring Boot dependencies
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.babyfish.jimmer:jimmer-spring-boot-starter:0.9.105")
    
    // Database
    runtimeOnly("org.postgresql:postgresql")
    
    // Testing
    testImplementation("io.github.truenine:composeserver-testtoolkit:0.0.26")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
```

#### Version Management with BOM

```kotlin
dependencies {
    // Import BOM for unified version management
    implementation(platform("io.github.truenine:composeserver-bom:0.0.26"))
    
    // No need to specify versions, managed by BOM
    implementation("io.github.truenine:composeserver-shared")
    implementation("io.github.truenine:composeserver-rds-shared")
    implementation("io.github.truenine:composeserver-rds-crud")
}
```

### 📝 Basic Usage Example

#### 1. Create Spring Boot Application

```kotlin
@SpringBootApplication
class ComposeServerApplication

fun main(args: Array<String>) {
    runApplication<ComposeServerApplication>(*args)
}
```

#### 2. Configure Database

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/compose_server
    username: postgres
    password: password
    driver-class-name: org.postgresql.Driver
    
jimmer:
  show-sql: true
  pretty-sql: true
  database-validation-mode: ERROR
```

#### 3. Create Entity

```kotlin
@Entity
@Table(name = "users")
interface User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long
    
    val username: String
    val email: String
    val createdAt: LocalDateTime
    val updatedAt: LocalDateTime
}
```

#### 4. Create Repository

```kotlin
@Repository
interface UserRepository : KRepository<User, Long> {
    fun findByUsername(username: String): User?
    fun existsByUsername(username: String): Boolean
}
```

#### 5. Create Service

```kotlin
@Service
@Transactional(readOnly = true)
class UserService(private val userRepository: UserRepository) {
    
    fun findById(id: Long): User? = userRepository.findNullable(id)
    
    @Transactional
    fun createUser(username: String, email: String): User {
        val user = User {
            username = username
            email = email
            createdAt = LocalDateTime.now()
            updatedAt = LocalDateTime.now()
        }
        return userRepository.save(user)
    }
}
```

### 🧪 Testing

```kotlin
@SpringBootTest
@Transactional
class UserServiceTest : IDatabasePostgresqlContainer {
    
    @Autowired
    private lateinit var userService: UserService
    
    @Test
    fun create_user_successfully() {
        // Given
        val username = "testuser"
        val email = "test@example.com"
        
        // When
        val user = userService.createUser(username, email)
        
        // Then
        assertThat(user.username).isEqualTo(username)
        assertThat(user.email).isEqualTo(email)
        assertThat(user.id).isNotNull()
    }
}
```

### 🚀 Run Application

```bash
# Start PostgreSQL with Docker
docker run --name postgres \
  -e POSTGRES_DB=compose_server \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=password \
  -p 5432:5432 \
  -d postgres:16

# Run application
./gradlew bootRun
```

---

## 📚 Documentation & Resources

### 📖 Documentation

- **[API Documentation](https://javadoc.io/doc/io.github.truenine)** - Complete Javadoc API documentation
- **[GitHub Repository](https://github.com/TrueNine/compose-server)** - Source code, examples, and latest updates
- **[Maven Central](https://central.sonatype.com/search?q=g:io.github.truenine)** - All published modules and versions
- **[Release Notes](https://github.com/TrueNine/compose-server/releases)** - Detailed version changelog

### 🎯 Module Selection Guide

**Basic Web Application**
```kotlin
implementation("io.github.truenine:composeserver-shared")
implementation("io.github.truenine:composeserver-rds-shared")
implementation("io.github.truenine:composeserver-rds-crud")
implementation("io.github.truenine:composeserver-security-spring")
```

**AI-Powered Application**
```kotlin
implementation("io.github.truenine:composeserver-shared")
implementation("io.github.truenine:composeserver-ai-shared")
implementation("io.github.truenine:composeserver-ai-langchain4j")
```

**Multi-Cloud Storage Application**
```kotlin
implementation("io.github.truenine:composeserver-shared")
implementation("io.github.truenine:composeserver-oss-shared")
implementation("io.github.truenine:composeserver-oss-minio")
implementation("io.github.truenine:composeserver-oss-aliyun-oss")
```

**E-commerce Payment Application**
```kotlin
implementation("io.github.truenine:composeserver-shared")
implementation("io.github.truenine:composeserver-pay-shared")
implementation("io.github.truenine:composeserver-pay-wechat")
implementation("io.github.truenine:composeserver-sms-shared")
implementation("io.github.truenine:composeserver-sms-tencent")
```

---

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

### 🔧 Development Setup

```bash
# Clone repository
git clone https://github.com/TrueNine/compose-server.git
cd compose-server

# Build project
./gradlew build

# Run tests
./gradlew test

# Format code
./gradlew spotlessApply
```

---

## 📄 License

This project is licensed under the [LGPL 2.1 License](LICENSE).

---

## 🌟 Support

- ⭐ Star this repository if you find it helpful
- 🐛 [Report issues](https://github.com/TrueNine/compose-server/issues)
- 💡 [Request features](https://github.com/TrueNine/compose-server/issues/new)
- 📧 Contact: [truenine304@gmail.com](mailto:truenine304@gmail.com)

---

<div align="center">

**Built with ❤️ by the Compose Server Team**

</div>
