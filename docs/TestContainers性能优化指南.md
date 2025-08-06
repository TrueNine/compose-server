# TestContainers 性能优化指南

TestContainers 是现代 Java/Kotlin 应用测试的重要工具，但其性能优化往往被忽视。本文档提供全面的性能优化策略，帮助显著提升测试执行速度。

## 快速优化清单

✅ **立即生效的优化**：

- [x] 启用容器重用：`testcontainers.reuse.enable=true`
- [ ] 增加启动超时时间到 30-60 秒
- [ ] 使用轻量级镜像（Alpine 版本）
- [ ] 配置 Docker 层缓存
- [ ] 启用并行测试执行

## 1. 全局配置优化

### 1.1 用户级配置文件 (~/.testcontainers.properties)

创建或更新用户主目录下的 `.testcontainers.properties` 文件：

```properties
# 基础性能配置
testcontainers.reuse.enable=true
testcontainers.reuse.hash=true

# Docker 连接优化
docker.client.strategy=org.testcontainers.dockerclient.UnixSocketStrategy
docker.host=unix:///var/run/docker.sock

# 资源限制优化
testcontainers.resource.limits.cpu=2
testcontainers.resource.limits.memory=2g

# 并行操作优化
testcontainers.parallel.mode=concurrent
testcontainers.parallel.max=4

# 镜像管理优化
testcontainers.pull.pause.timeout=300s
testcontainers.image.substitutor.enable=true

# 网络优化
testcontainers.host.override=localhost
testcontainers.checks.disable=false

# 日志优化
testcontainers.logs.enabled=false
testcontainers.startup.logs.verbose=false
```

### 1.2 项目级配置文件

在项目根目录创建 `testcontainers.properties`：

```properties
# 项目特定优化
testcontainers.reuse.enable=true

# 容器启动优化
testcontainers.startup.timeout=60s
testcontainers.connect.timeout=30s
testcontainers.read.timeout=120s

# 项目特定镜像配置
testcontainers.image.prefix=local/
testcontainers.image.cache.enable=true
```

## 2. 容器启动优化

### 2.1 超时配置优化

**问题识别**：当前默认 10 秒启动超时过短，导致启动失败和重试。

**解决方案**：

```kotlin
// 在容器配置中增加超时时间
val container = PostgreSQLContainer<Nothing>(DockerImageName.parse(image))
  .withStartupTimeout(Duration.ofSeconds(60))  // 从 10 秒提升到 60 秒
  .withConnectTimeout(Duration.ofSeconds(30))
```

### 2.2 等待策略优化

```kotlin
// 优化等待策略，减少轮询频率
val container = GenericContainer(image)
  .waitingFor(
    Wait.forLogMessage(".*ready.*", 1)
      .withStartupTimeout(Duration.ofSeconds(60))
      .withPollInterval(Duration.ofSeconds(2))  // 增加轮询间隔
  )
```

### 2.3 健康检查优化

```kotlin
// 使用更高效的健康检查
val container = GenericContainer(image)
  .withCommand("--health-check", "true")
  .waitingFor(Wait.forHealthcheck())
  .withStartupTimeout(Duration.ofSeconds(45))
```

## 3. 镜像管理优化

### 3.1 选择轻量级镜像

**推荐镜像版本**：

```yaml
# 优先使用 Alpine 版本（体积小，启动快）
postgres: "postgres:17.5-alpine3.22"          # ~150MB vs 400MB+
mysql: "mysql:8.4.6"                           # 官方优化版本
redis: "redis:8.0.3-alpine3.21"               # ~40MB vs 130MB+
minio: "minio/minio:RELEASE.2025-07-23T15-54-02Z"  # 最新稳定版
```

### 3.2 镜像预拉取策略

创建预拉取脚本 `scripts/pull-test-images.sh`：

```bash
#!/bin/bash
# TestContainers 镜像预拉取脚本

echo "预拉取 TestContainers 镜像..."

# 定义镜像列表
images=(
  "postgres:17.5-alpine3.22"
  "mysql:8.4.6-oraclelinux9"
  "redis:8.0.3-alpine3.21"
  "minio/minio:RELEASE.2025-07-23T15-54-02Z"
)

# 并行拉取镜像
for image in "${images[@]}"; do
  echo "拉取镜像: $image"
  docker pull "$image" &
done

# 等待所有拉取完成
wait
echo "所有测试镜像拉取完成！"

# 清理未使用的镜像
docker image prune -f
```

### 3.3 镜像缓存优化

```properties
# Docker 构建缓存优化
DOCKER_BUILDKIT=1
BUILDKIT_INLINE_CACHE=1

# Docker 层缓存配置
docker.layer.cache.enable=true
docker.layer.cache.size=5GB
```

## 4. 并行测试优化

### 4.1 Gradle 并行配置

在 `gradle.properties` 中配置：

```properties
# 启用并行构建
org.gradle.parallel=true
org.gradle.workers.max=4

# JVM 优化
org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=1g -XX:+UseG1GC

# 测试并行执行
org.gradle.test.parallel.enabled=true
org.gradle.test.max.parallel.forks=2
```

### 4.2 JUnit 5 并行配置

创建 `src/test/resources/junit-platform.properties`：

```properties
# JUnit 5 并行执行配置
junit.jupiter.execution.parallel.enabled=true
junit.jupiter.execution.parallel.mode.default=concurrent
junit.jupiter.execution.parallel.mode.classes.default=concurrent

# 线程池配置
junit.jupiter.execution.parallel.config.strategy=dynamic
junit.jupiter.execution.parallel.config.dynamic.factor=2
```

### 4.3 TestContainers 并发优化

```kotlin
// 容器并发启动工具
class ParallelContainerStarter {
  companion object {
    fun startContainers(vararg containers: GenericContainer<*>) {
      containers.toList().parallelStream().forEach { container ->
        container.start()
      }
    }
  }
}

// 使用示例
@BeforeAll
fun setupContainers() {
  ParallelContainerStarter.startContainers(
    postgresContainer,
    redisContainer,
    minioContainer
  )
}
```

## 5. 性能监控和调试

### 5.1 容器启动时间监控

```kotlin
class ContainerPerformanceMonitor {
  companion object {
    fun <T : GenericContainer<T>> T.withPerformanceMonitoring(): T {
      val startTime = System.currentTimeMillis()

      return this.apply {
        withLogConsumer { frame ->
          if (frame.utf8String.contains("ready")) {
            val duration = System.currentTimeMillis() - startTime
            println("容器 ${this.dockerImageName} 启动耗时: ${duration}ms")
          }
        }
      }
    }
  }
}
```

### 5.2 资源使用监控

```kotlin
// 容器资源监控
fun GenericContainer<*>.monitorResources() {
  val stats = this.dockerClient.statsCmd(this.containerId)
  // 记录 CPU、内存使用情况
}
```

### 5.3 调试工具

```kotlin
// 容器调试信息收集
class ContainerDebugger {
  fun printContainerInfo(container: GenericContainer<*>) {
    println(
      """
            容器调试信息:
            - 镜像: ${container.dockerImageName}
            - 状态: ${container.isRunning}
            - 端口映射: ${container.exposedPorts}
            - 启动命令: ${container.commandParts}
            - 环境变量: ${container.envMap}
        """.trimIndent()
    )
  }
}
```

## 6. CI/CD 环境优化

### 6.1 GitHub Actions 优化

```yaml
# .github/workflows/test.yml
name: Tests
on: [ push, pull_request ]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      # Docker 层缓存
      - name: Set up Docker Layer Caching
        uses: actions/cache@v4
        with:
          path: /tmp/.buildx-cache
          key: ${{ runner.os }}-buildx-${{ github.sha }}
          restore-keys: ${{ runner.os }}-buildx-

      # 预拉取镜像
      - name: Pull test images
        run: |
          docker pull postgres:17.5-alpine3.22 &
          docker pull redis:8.0.3-alpine3.21 &
          wait

      # 运行测试
      - name: Run tests
        run: ./gradlew test -Pci=true
        env:
          TESTCONTAINERS_RYUK_DISABLED: true  # CI 环境禁用 Ryuk
```

### 6.2 Jenkins 优化

```groovy
pipeline {
    agent any
    
    environment {
        DOCKER_BUILDKIT = '1'
        TESTCONTAINERS_RYUK_DISABLED = 'true'
    }
    
    stages {
        stage('Setup') {
            parallel {
                stage('Pull Images') {
                    steps {
                        sh 'scripts/pull-test-images.sh'
                    }
                }
                stage('Setup Gradle') {
                    steps {
                        sh './gradlew --version'
                    }
                }
            }
        }
        
        stage('Test') {
            steps {
                sh './gradlew test --parallel --max-workers=4'
            }
        }
    }
}
```

## 7. 故障排查指南

### 7.1 常见性能问题

**问题 1：容器启动超时**

```
症状：Could not start container within 10 seconds
解决：增加启动超时时间到 60 秒
```

**问题 2：端口冲突**

```
症状：Port already in use
解决：使用动态端口分配，避免固定端口
```

**问题 3：内存不足**

```
症状：OutOfMemoryError 或容器启动失败
解决：增加 JVM 堆内存，限制并发容器数量
```

### 7.2 性能诊断命令

```bash
# 查看 Docker 资源使用
docker stats

# 查看容器启动日志
docker logs <container_id>

# 检查镜像大小
docker images --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}"

# 清理 Docker 资源
docker system prune -af
```

### 7.3 调试配置

在测试中启用详细日志：

```yaml
# application-test.yml
logging:
  level:
    org.testcontainers: DEBUG
    com.github.dockerjava: DEBUG
  pattern:
    console: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
```

## 8. 最佳实践总结

### 8.1 开发环境

- ✅ 启用容器重用
- ✅ 使用轻量级镜像
- ✅ 预拉取常用镜像
- ✅ 配置合理的超时时间

### 8.2 CI/CD 环境

- ✅ 禁用 Ryuk（资源清理器）
- ✅ 启用 Docker 层缓存
- ✅ 限制并发容器数量
- ✅ 使用镜像预拉取

### 8.3 团队协作

- ✅ 统一镜像版本配置
- ✅ 共享性能优化配置
- ✅ 建立性能监控机制
- ✅ 定期清理 Docker 资源

## 9. 性能指标

通过以上优化，预期可实现：

- **启动时间减少**：30-50%
- **资源使用降低**：20-30%
- **测试稳定性提升**：显著减少启动失败
- **CI/CD 加速**：整体构建时间减少 20-40%

---

## 参考资源

- [TestContainers 官方文档](https://testcontainers.com/)
- [Docker 性能优化指南](https://docs.docker.com/config/containers/resource_constraints/)
- [Gradle 测试优化](https://docs.gradle.org/current/userguide/performance.html#parallel_execution)
