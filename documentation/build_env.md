## 环境准备

```text
java: 23.0.1+13
kotlin: 2.1.0
gradle: 8.11.1
maven: 3.9.9
gpg4
```

> 如无需推送到制品仓库，则 gnugpg 无需安装配置

- 所有构建链当中，路径不要存在任何非 ISO8859-1 字符，包括中文等的
- 将 本地 java 配置到 `JAVA_HOME` 且 保证版本至少为 21，并包含 `bin` 在执行路径中
- 将 本地 gradle 安装路径配置到环境变量 `GRADLE_HOME`
- 将 本地 .gradle 路径配置到 `GRADLE_USER_HOME`
- 将 本地 maven 路径配置到 `MVN_HOME`
- 将项目内的 `init.gradle.bak` 以及 `gradle.properties.bak` copy 到 本地 .gradle 下
- 将 `init.gradle.bak` 以及 `gradle.properties.bak` 去除 `.bak` 后缀
- 使用 `gpg` 设置或生成证书并上传至服务器，使用 `gpg --list-secret-keys` 查看已生成的证书
- 将 `gradle.properties` 的所有值填写完整，亦可自行更改项目内的取值，达到相同效果即可

```shell
# 1. 初始化项目
gradle init

# 2. 生成 gradle wrapper
gradle wrapper

# 3. 检查当前项目所有测试是否可以正常运行
./gradlew check
```

> 如果网络问题，gradle可以[配置代理](/documentation/gradle配置本地代理.md)

## 使用技巧

鉴于很多时候，都不太会使用 gradle，以下为一些不太常用的技巧

### settings.gradle.kts 配置 gradle plugin repositories

```kotlin
dependencyResolutionManagement {
  repositories {
    // ... 所有所在仓狂
  }
  // （可选）引入 version-catalog
  versionCatalogs { create("libs") { from("net.yan100.compose:version-catalog:sdk版本") } }
}
```

所有 版本以及依赖交由 gradle version catalog
管理，请参考 [gradle version catalog](https://docs.gradle.org/current/userguide/dependency_management_basics.html#version_catalog)

### version-catalog 依赖引入方式

```kotlin
plugins {
  // gradle 插件 引入方式
  alias(libs.plugins.net.yan100.compose.gradlePlugin)
  alias(libs.plugins.org.springframework.boot)
  alias(libs.plugins.io.spring.dependencyManagement)
}

// 全新的依赖引入方式
dependencies {
  // 正常依赖引入方式
  implementation(libs.net.yan100.compose.security)

  // 引入一组依赖捆绑包
  implementation(libs.bundles.kotlin)

  // 引入 maven bom...
  implementation(platform(libs.org.springframework.boot.springBootDependencies))

  // 引入 特定 分类器版本的依赖
  implementation(variantOf(libs.net.yan100.compose.rds) { classifier("postgresql") })

  // ksp 或 kapt 同样支持此类引入方式
  ksp(libs.net.yan100.compose.ksp)
}
```
