# Compose Server

## 项目介绍

这是一个基于 以下技术构建的服务器端 sdk

- [Kotlin](https://kotlinlang.org/)
- [Gradle](https://gradle.org/)
- [SpringBoot](https://spring.io/projects/spring-boot)

其采用多项目模块化设计

## 使用

首先，本项目目前由于种种原因，没有上架到 maven 中央仓库（流程过于复杂），目前只在私有仓库。
但先写在这里。

- settings.gradle.kts 配置

```kotlin
dependencyResolutionManagement {
  repositories {
    // ... 所有所在仓狂
  }
  versionCatalogs { create("libs") { from("net.yan100.compose:version-catalog:sdk版本") } }
}
```

> 所有 版本以及依赖交由 gradle version catalog
> 管理，请参考 [gradle version catalog](https://docs.gradle.org/current/userguide/dependency_management_basics.html#version_catalog)

- build.gradle.kts 配置

```kotlin
plugins {
  alias(libs.plugins.net.yan100.compose.gradlePlugin) // gradle 插件
  alias(libs.plugins.org.springframework.boot)
  alias(libs.plugins.io.spring.dependencyManagement)
  // ... 其他插件可以如上进行找到，sdk 使用了全限定名的配置
  // 其他插件可自定义
}

// 全新的依赖引入方式
dependencies {
  // libs 已经暴露了大多经过测试的依赖版本
  // 引入方式如下所示

  // 引入捆绑包形式
  implementation(libs.bundles.kotlin)
  // 引入 maven bom...
  implementation(platform(libs.org.springframework.boot.springBootDependencies))
  // 正常依赖引入方式
  implementation(libs.net.yan100.compose.security)
  // 引入 特定 分类器版本的依赖
  implementation(variantOf(libs.net.yan100.compose.rds) { classifier("postgresql") })
  // ksp 或 kapt 同样支持此类引入方式
  ksp(libs.net.yan100.compose.ksp)
}
```

## 审阅代码环境要求

```envRequirement
java: 21.0.2
kotlin: 2.0.20
gradle: 8.10.1
```

> 注：开发机请准备 16GB 内存或以上，磁盘空出 10G 以上（windows 请在 C盘 留下 10G
> 空间）。
> 如果使用 IDEA，请分配 8G 内存

## 环境准备

- 确保系统的 JAVA_HOME 环境变量 至少为 JDK21+

> 注：windows 在 path 内可以调整变量的优先级

- 安装 gradle 并将其配置到环境变量 `GRADLE_HOME`

> 注：windows 将其配置到 path 内

- init 对项目进行初始化

```shell
# 初始化项目
gradle init

# 生成 gradle wrapper
gradle wrapper

# 检查当前项目
./gradlew check
```
