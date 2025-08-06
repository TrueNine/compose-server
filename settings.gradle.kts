pluginManagement {
  includeBuild("build-logic")
  repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://repo.spring.io/milestone")
  }
}

plugins { id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0" }

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "compose-server"

// 根级模块
listOf("testtoolkit", "version-catalog", "shared", "bom", "gradle-plugin", "cacheable").forEach { p ->
  include(p)
  findProject(":$p")?.name = p
}

// 监控模块
include("surveillance:surveillance-shared")

findProject(":surveillance:surveillance-shared")?.name = "surveillance-shared"

include("surveillance:surveillance-hikvision")

findProject(":surveillance:surveillance-hikvision")?.name = "surveillance-hikvision"

// 短信服务
include("sms:sms-shared")

findProject(":sms:sms-shared")?.name = "sms-shared"

include("sms:sms-tencent")

findProject(":sms:sms-tencent")?.name = "sms-tencent"

// ai 服务
include("ai:ai-shared")

findProject(":ai:ai-shared")?.name = "ai-shared"

include("ai:ai-langchain4j")

findProject(":ai:ai-langchain4j")?.name = "ai-langchain4j"

// 支付服务
include("pay:pay-shared")

findProject(":pay:pay-shared")?.name = "pay-shared"

include("pay:pay-wechat")

findProject(":pay:pay-wechat")?.name = "pay-wechat"

// 对象存储服务
include("oss:oss-shared")

findProject(":oss:oss-shared")?.name = "oss-shared"

include("oss:oss-minio")

findProject(":oss:oss-minio")?.name = "oss-minio"

include("oss:oss-aliyun-oss")

findProject(":oss:oss-aliyun-oss")?.name = "oss-aliyun-oss"

include("oss:oss-huawei-obs")

findProject(":oss:oss-huawei-obs")?.name = "oss-huawei-obs"

include("oss:oss-volcengine-tos")

findProject(":oss:oss-volcengine-tos")?.name = "oss-volcengine-tos"

// 关系型数据库服务
include("rds:rds-shared")

findProject(":rds:rds-shared")?.name = "rds-shared"

include("rds:rds-crud")

findProject(":rds:rds-crud")?.name = "rds-crud"

include("rds:rds-jimmer-ext-postgres")

findProject(":rds:rds-jimmer-ext-postgres")?.name = "rds-jimmer-ext-postgres"

include("rds:rds-flyway-migration-postgresql")

findProject(":rds:rds-flyway-migration-postgresql")?.name = "rds-flyway-migration-postgresql"

include("rds:rds-flyway-migration-mysql8")

findProject(":rds:rds-flyway-migration-mysql8")?.name = "rds-flyway-migration-mysql8"

include("rds:rds-flyway-migration-shared")

findProject(":rds:rds-flyway-migration-shared")?.name = "rds-flyway-migration-shared"

// 数据采集器
include("data:data-crawler")

findProject(":data:data-crawler")?.name = "data-crawler"

include("data:data-extract")

findProject(":data:data-extract")?.name = "data-extract"

// 安全模块
include("security:security-spring")

findProject(":security:security-spring")?.name = "security-spring"

include("security:security-oauth2")

findProject(":security:security-oauth2")?.name = "security-oauth2"

include("security:security-crypto")

findProject(":security:security-crypto")?.name = "security-crypto"

// 特定依赖处理
include("depend:depend-servlet")

findProject(":depend:depend-servlet")?.name = "depend-servlet"

include("depend:depend-paho")

findProject(":depend:depend-paho")?.name = "depend-paho"

include("depend:depend-http-exchange")

findProject(":depend:depend-http-exchange")?.name = "depend-http-exchange"

include("depend:depend-jackson")

findProject(":depend:depend-jackson")?.name = "depend-jackson"

include("depend:depend-springdoc-openapi")

findProject(":depend:depend-springdoc-openapi")?.name = "depend-springdoc-openapi"

include("depend:depend-xxl-job")

findProject(":depend:depend-xxl-job")?.name = "depend-xxl-job"

// ksp
include("ksp:ksp-plugin")

findProject(":ksp:ksp-plugin")?.name = "ksp-plugin"

include("ksp:ksp-shared")

findProject(":ksp:ksp-shared")?.name = "ksp-shared"

include("ksp:ksp-meta")

findProject(":ksp:ksp-meta")?.name = "ksp-meta"
