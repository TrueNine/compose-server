pluginManagement {
  includeBuild("build-logic")
  includeBuild("gradleplugin/gradleplugin-dotenv")
  repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://repo.spring.io/milestone")
  }
}

plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
  // See https://central.sonatype.com/artifact/io.gitee.zjarlin.auto-modules/io.gitee.zjarlin.auto-modules.gradle.plugin
  // id("io.gitee.zjarlin.auto-modules") version "0.0.616"
}

// Disable parallel project execution when dependencyUpdates tasks are requested because the plugin cannot run in parallel.
val startParameter = gradle.startParameter

if (startParameter.taskNames.any { it.contains("dependencyUpdates", ignoreCase = true) }) {
  startParameter.isParallelProjectExecutionEnabled = false
}

// autoModules { excludeModules("build-logic") }

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "compose-server"

// 根级模块
listOf("version-catalog", "shared", "bom", "cacheable", "docsite").forEach { p ->
  include(p)
  findProject(":$p")?.name = p
}

// Gradle 插件模块
include("gradleplugin:gradleplugin-composeserver")

// 监控模块
include("surveillance:surveillance-shared")

include("surveillance:surveillance-hikvision")

// 短信服务
include("sms:sms-shared")

include("sms:sms-tencent")

// ai 服务
include("ai:ai-shared")

include("ai:ai-langchain4j")

// 支付服务
include("pay:pay-shared")

include("pay:pay-wechat")

// 对象存储服务
include("oss:oss-shared")

include("oss:oss-minio")

include("oss:oss-aliyun-oss")

include("oss:oss-huawei-obs")

include("oss:oss-volcengine-tos")

// 关系型数据库服务
include("rds:rds-shared")

include("rds:rds-crud")

include("rds:rds-jimmer-ext-postgres")

include("rds:rds-flyway-migration-postgresql")

include("rds:rds-flyway-migration-mysql8")

include("rds:rds-flyway-migration-shared")

// 数据采集器
include("data:data-crawler")

include("data:data-extract")

// 安全模块
include("security:security-spring")

include("security:security-oauth2")

include("security:security-crypto")

// 特定依赖处理
include("depend:depend-servlet")

include("depend:depend-paho")

include("depend:depend-http-exchange")

include("depend:depend-jackson")

include("depend:depend-springdoc-openapi")

include("depend:depend-xxl-job")

// ksp
include("ksp:ksp-plugin")

include("ksp:ksp-shared")

include("ksp:ksp-meta")

// platform sdk
include("psdk:psdk-wxpa")

// IDE 插件模块
include("ide:ide-idea-mcp")

// 测试工具
include("testtoolkit:testtoolkit-shared")

include("testtoolkit:testtoolkit-testcontainers")

include("testtoolkit:testtoolkit-springmvc")

// === 集成测试模块 ===
include("integrate-test:depend:jackson")

include("integrate-test:oss:minio")

include("integrate-test:oss:volcengine-tos")

include("integrate-test:cacheable")
