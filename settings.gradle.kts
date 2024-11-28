plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

dependencyResolutionManagement { versionCatalogs { create("libs") { from(files("libs.versions.toml")) } } }

rootProject.name = "compose-server"
includeBuild("version-catalog")



findProject(":gradle-plugin")?.name = "gradle-plugin"

listOf(
  "test-toolkit",
  "core",
  "gradle-plugin",
  "oss",
  "cacheable",
  "cacheable",
  "pay"
).forEach { p ->
  include(p)
  findProject(":$p")?.name = p
}

("rds" to listOf(
  "core", "crud", "jimmer", "migration-mysql", "migration-postgres"
)).apply {
  second.forEach { n ->
    include("$first:$n")
    findProject(":$first:$n")?.name = "$first-$n"
  }
}

// 数据采集器
("data" to listOf(
  "crawler", "extract"
)).apply {
  second.forEach { n ->
    include("$first:$n")
    findProject(":$first:$n")?.name = "$first-$n"
  }
}

// 安全相关
("security" to listOf(
  "spring", "oauth2", "crypto"
)).apply {
  second.forEach { n ->
    include("$first:$n")
    findProject(":$first:$n")?.name = "$first-$n"
  }
}

// 特定依赖处理
("depend" to listOf(
  "servlet", "paho", "http-exchange", "jsr303-validation", "jackson", "springdoc-openapi", "xxl-job"
)).apply {
  second.forEach { n ->
    include("$first:$n")
    findProject(":$first:$n")?.name = "$first-$n"
  }
}

// ksp
("ksp" to listOf(
  "plugin", "core", "toolkit"
)).apply {
  second.forEach { n ->
    include("$first:$n")
    findProject(":$first:$n")?.name = "$first-$n"
  }
}
