# Volcengine TOS Integration

高性能的火山引擎 TOS（对象存储服务）Spring Boot 集成，提供生产级别的配置和详细的日志记录。

[官方文档](https://www.volcengine.com/docs/6349/74836)
[java SDK 文档](https://www.volcengine.com/docs/6349/79895)
[分片上传](https://www.volcengine.com/docs/6349/79922)

## 特性

- 🚀 **高性能配置**: 优化的连接池、超时设置和重试策略
- 🔒 **安全性**: 支持 SSL/TLS、CRC 校验、证书验证
- 📊 **详细日志**: 完整的配置和运行时日志记录
- 🌐 **多种认证**: 支持静态凭证和 STS 临时凭证
- 🔧 **灵活配置**: 支持代理、自定义域名、User-Agent 定制
- ✅ **生产就绪**: 针对不同环境的优化配置模板

## 快速开始

### 1. 添加依赖

```kotlin
dependencies {
  implementation("io.github.truenine.composeserver:oss-volcengine-tos")
}
```

### 2. 基本配置

```yaml
compose:
  oss:
    provider: volcengine-tos
    endpoint: tos-cn-beijing.volces.com
    region: cn-beijing
    access-key: ${TOS_ACCESS_KEY}
    secret-key: ${TOS_SECRET_KEY}
```

### 3. 使用服务

```kotlin
@Service
class FileService(
  private val objectStorageService: ObjectStorageService
) {
  fun uploadFile(bucketName: String, objectKey: String, inputStream: InputStream) {
    objectStorageService.putObject(bucketName, objectKey, inputStream)
  }
}
```

## 安全配置

⚠️ **重要安全提醒**：

- **绝不要**在代码中硬编码 Access Key 和 Secret Key
- **绝不要**将包含敏感信息的配置文件提交到版本控制系统
- 使用环境变量或安全的配置管理系统来管理敏感信息

### 环境变量设置

```bash
# 设置环境变量
export VOLCENGINE_TOS_ACCESS_KEY="your_access_key_here"
export VOLCENGINE_TOS_SECRET_KEY="your_secret_key_here"

# 运行集成测试
./gradlew :oss:volcengine-tos:test --tests "*IntegrationTest"
```

## 配置参数详解

### 认证配置

| 参数              | 类型     | 必选 | 默认值 | 说明         |
|-----------------|--------|----|-----|------------|
| `access-key`    | String | 是  | -   | 访问密钥 ID（使用环境变量）    |
| `secret-key`    | String | 是  | -   | 私有访问密钥（使用环境变量）     |
| `session-token` | String | 否  | -   | STS 临时凭证令牌 |

### 网络配置

| 参数                           | 类型  | 默认值   | 说明           |
|------------------------------|-----|-------|--------------|
| `connect-timeout-mills`      | int | 10000 | 连接超时时间（毫秒）   |
| `read-timeout-mills`         | int | 60000 | 读取超时时间（毫秒）   |
| `write-timeout-mills`        | int | 60000 | 写入超时时间（毫秒）   |
| `idle-connection-time-mills` | int | 60000 | 空闲连接超时时间（毫秒） |

### 连接池配置

| 参数                       | 类型  | 默认值  | 说明           |
|--------------------------|-----|------|--------------|
| `max-connections`        | int | 1024 | 最大连接数        |
| `max-retry-count`        | int | 3    | 最大重试次数       |
| `dns-cache-time-minutes` | int | 5    | DNS 缓存时间（分钟） |

### 功能开关

| 参数                                   | 类型      | 默认值   | 说明          |
|--------------------------------------|---------|-------|-------------|
| `enable-ssl`                         | boolean | true  | 启用 SSL/TLS  |
| `enable-crc`                         | boolean | true  | 启用 CRC64 校验 |
| `enable-verify-ssl`                  | boolean | true  | 启用 SSL 证书验证 |
| `client-auto-recognize-content-type` | boolean | true  | 自动识别内容类型    |
| `enable-logging`                     | boolean | false | 启用详细日志      |

## 环境配置示例

### 开发环境

```yaml
compose:
  oss:
    provider: volcengine-tos
    volcengine-tos:
      endpoint: tos-cn-beijing.volces.com
      region: cn-beijing
      access-key: ${TOS_ACCESS_KEY}
      secret-key: ${TOS_SECRET_KEY}
      enable-logging: true
      max-connections: 50
      dns-cache-time-minutes: 0
```

### 生产环境

```yaml
compose:
  oss:
    provider: volcengine-tos
    volcengine-tos:
      endpoint: ${TOS_ENDPOINT}
      region: ${TOS_REGION}
      access-key: ${TOS_ACCESS_KEY}
      secret-key: ${TOS_SECRET_KEY}
      exposed-base-url: ${TOS_CDN_DOMAIN}

      # 高性能配置
      connect-timeout-mills: 8000
      read-timeout-mills: 45000
      write-timeout-mills: 45000
      max-connections: 2048
      max-retry-count: 2
      dns-cache-time-minutes: 10

      # 安全配置
      enable-ssl: true
      enable-crc: true
      enable-verify-ssl: true
      enable-logging: false

      # User-Agent 定制
      user-agent-product-name: MyApp
      user-agent-soft-version: ${app.version}
      user-agent-customized-key-values:
        environment: production
        cluster: ${CLUSTER_NAME}
        region: ${TOS_REGION}
```

## 高级功能

### STS 临时凭证

```yaml
compose:
  oss:
    volcengine-tos:
      access-key: ${STS_ACCESS_KEY}
      secret-key: ${STS_SECRET_KEY}
      session-token: ${STS_SESSION_TOKEN}
```

### 代理配置

```yaml
compose:
  oss:
    volcengine-tos:
      proxy-host: proxy.company.com
      proxy-port: 8080
      proxy-user-name: ${PROXY_USERNAME}
      proxy-password: ${PROXY_PASSWORD}
```

### 自定义域名

```yaml
compose:
  oss:
    volcengine-tos:
      custom-domain: oss.mycompany.com
      is-custom-domain: true
      exposed-base-url: https://cdn.mycompany.com
```

## 日志配置

启用详细日志以便调试：

```yaml
compose:
  oss:
    volcengine-tos:
      enable-logging: true

logging:
  level:
    io.github.truenine.composeserver.oss.volcengine: DEBUG
    com.volcengine.tos: INFO
```

## 性能调优建议

### 高并发场景

```yaml
compose:
  oss:
    volcengine-tos:
      max-connections: 2048
      connect-timeout-mills: 5000
      read-timeout-mills: 30000
      write-timeout-mills: 30000
      max-retry-count: 2
      dns-cache-time-minutes: 15
```

### 大文件传输

```yaml
compose:
  oss:
    volcengine-tos:
      read-timeout-mills: 300000   # 5分钟
      write-timeout-mills: 300000  # 5分钟
      max-retry-count: 1
```

### 低延迟场景

```yaml
compose:
  oss:
    volcengine-tos:
      connect-timeout-mills: 3000
      read-timeout-mills: 10000
      write-timeout-mills: 10000
      dns-cache-time-minutes: 30
      enable-crc: false  # 牺牲数据完整性检查换取性能
```
