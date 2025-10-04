# Compose Server

Kotlin modular library collection for enterprise server development.

## What it is

30+ modules providing common enterprise functionality:
- Database operations (Jimmer ORM, PostgreSQL, MySQL)
- Object storage (MinIO, Aliyun OSS, Huawei OBS, Volcengine TOS)
- AI integration (LangChain4j)
- Security (Spring Security, OAuth2, crypto)
- Payment (WeChat Pay)
- SMS (Tencent Cloud)
- Caching, data processing, web crawling

## Requirements

- JDK 17+
- Kotlin 2.2.0+
- Spring Boot 3.5.0+

## Usage

```kotlin
dependencies {
  implementation(platform("io.github.truenine:composeserver-bom:0.0.26"))
  implementation("io.github.truenine:composeserver-shared")
  // Add other modules as needed
}
```

## Build

```bash
./gradlew build
```

## License

LGPL 2.1