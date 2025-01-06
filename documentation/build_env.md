此说明为希望为项目提供贡献，需在本地构建工程而作步骤说明，因为发现 gradle 应用的真的不多
（如仅是使用，则默认都会以下构建）。我尽量减少所带来的心智负担。

> 注意：
> - 所有依赖版本均可在 [version-catalog](../gradle/libs.versions.toml) 中找到对应显眼的版本号，
> - 所有构建链目录或文件路径等当中，非必要不使用任何`非ASCII字符`

### java

将 本地 java 配置到 `JAVA_HOME` 并包含 `bin` 在执行路径中

### maven

> gradle 的所有构件存储于 maven 仓库，所有才会使用到 maven

- （可选）将 本地 maven 路径配置到 `MVN_HOME`

### gradle

- （可选）配置本地 .gradle.properties 文件，[参考](./example/gradle.properties.example)
- （可选）配置本地 init.gradle.kts 文件，[参考](./example/init.gradle.kts.example)
- （可选）如遇网络问题，gradle可以[配置 gradle properties](./example/gradle.properties.example) 进行代理配置
- （可选）使用 `gpg` [生成密钥](./gpg_key_generate.md)

### gpg

> 如无需推送到制品仓库，则 gpg 无需安装配置

## 构建启动项目

```shell
# 1. 使用本地gradle初始化项目
gradle init

# 2. 使用本地gradle为项目生成 gradle wrapper
gradle wrapper

# 3. 检查当前项目所有测试是否可以正常运行
./gradlew check
```

> 一般，IDE会替你完成上述步骤，但如果出现其他问题，可按照上述 命令执行进行反复尝试

## gradle 的一些使用指南

鉴于很多时候，都不太会使用 gradle，以下为一些不太常用的技巧

### settings.gradle.kts

```kotlin
dependencyResolutionManagement {
  repositories {
    mavenCentral()
    maven(url = uri("your-repository-url"))
    // ...
  }
  // 引入 version-catalog
  versionCatalogs { create("libs") { from("net.yan100.compose:version-catalog:sdk版本") } }
}
```

所有 版本以及依赖交由 gradle version catalog
管理，可参考 [gradle version catalog](https://docs.gradle.org/current/userguide/dependency_management_basics.html#version_catalog)

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
