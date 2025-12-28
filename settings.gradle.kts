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
  id("org.gradle.toolchains.foojay-resolver-convention") version "+"
  // See https://central.sonatype.com/artifact/io.gitee.zjarlin.auto-modules/io.gitee.zjarlin.auto-modules.gradle.plugin
  //id("site.addzero.gradle.plugin.modules-buddy") version "+"
}

// Disable parallel project execution when dependencyUpdates tasks are requested because the plugin cannot run in parallel.
val startParameter = gradle.startParameter

if (startParameter.taskNames.any { it.contains("dependencyUpdates", ignoreCase = true) }) {
  startParameter.isParallelProjectExecutionEnabled = false
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "compose-server"

// Root-level modules
listOf("version-catalog", "shared", "bom", "cacheable", "docsite").forEach { p ->
  include(p)
  findProject(":$p")?.name = p
}

// Gradle plugin modules
include("gradleplugin:gradleplugin-composeserver")

// Surveillance modules
include("surveillance:surveillance-shared")

include("surveillance:surveillance-hikvision")

// SMS services
include("sms:sms-shared")

include("sms:sms-tencent")

// AI services
include("ai:ai-shared")

include("ai:ai-langchain4j")

// Payment services
include("pay:pay-shared")

include("pay:pay-wechat")

// Object storage services
include("oss:oss-shared")

include("oss:oss-minio")

include("oss:oss-aliyun-oss")

include("oss:oss-huawei-obs")

include("oss:oss-volcengine-tos")

// Relational database services
include("rds:rds-shared")

include("rds:rds-crud")

include("rds:rds-jimmer-ext-postgres")

include("rds:rds-flyway-migration-postgresql")

include("rds:rds-flyway-migration-mysql8")

include("rds:rds-flyway-migration-shared")

// Data collectors
include("data:data-crawler")

include("data:data-extract")

// Security modules
include("security:security-spring")

include("security:security-oauth2")

include("security:security-crypto")

// Specialized dependency modules
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

// IDE plugin modules
include("ide:ide-idea-mcp")

// test toolkit
include("testtoolkit:testtoolkit-shared")

include("testtoolkit:testtoolkit-testcontainers")

include("testtoolkit:testtoolkit-springmvc")

// === Integration test modules ===
include("integrate-test:depend:jackson")

include("integrate-test:oss:minio")

include("integrate-test:oss:volcengine-tos")

include("integrate-test:cacheable")
