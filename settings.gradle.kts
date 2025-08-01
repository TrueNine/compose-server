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

fun Pair<String, List<String>>.useFile() {
  second.forEach { n ->
    include("$first:$n")
    findProject(":$first:$n")?.name = "$first-$n"
  }
}

listOf("testtoolkit", "version-catalog", "shared", "bom", "gradle-plugin", "cacheable", "cacheable").forEach { p ->
  include(p)
  findProject(":$p")?.name = p
}

// 监控模块
("surveillance" to listOf("shared", "hikvision")).useFile()

// 短信服务
("sms" to listOf("tencent", "shared")).useFile()

// ai 服务
("ai" to listOf("shared", "langchain4j")).useFile()

// 支付服务
("pay" to listOf("shared", "wechat")).useFile()

// 对象存储服务
("oss" to listOf("shared", "minio", "aliyun-oss", "huawei-obs", "volcengine-tos")).useFile()

// 关系型数据库服务
("rds" to listOf("shared", "crud", "jimmer-ext-postgres", "flyway-migration-postgresql", "flyway-migration-mysql8", "flyway-migration-shared")).useFile()

// 数据采集器
("data" to listOf("crawler", "extract")).useFile()

// 安全模块
("security" to listOf("spring", "oauth2", "crypto")).useFile()

// 特定依赖处理
("depend" to listOf("servlet", "paho", "http-exchange", "jackson", "springdoc-openapi", "xxl-job")).useFile()

// ksp
("ksp" to listOf("plugin", "shared", "meta")).useFile()
