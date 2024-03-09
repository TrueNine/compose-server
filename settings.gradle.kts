pluginManagement {
  val release = "https://packages.aliyun.com/maven/repository/2336368-release-CiFRF5/"

  repositories {
    mavenLocal()
    maven(url = uri("https://repo.huaweicloud.com/repository/maven/"))
    maven(url = uri("https://repo.spring.io/milestone"))
    maven(url = uri(release)) {
      isAllowInsecureProtocol = true
      credentials {
        username = System.getenv("YUNXIAO_USER")
        password = System.getenv("YUNXIAO_PWD")
      }
    }
    gradlePluginPortal()
    mavenCentral()
  }
}

dependencyResolutionManagement {
  versionCatalogs { create("libs") { from(files("gradle-plugin/libs.versions.toml")) } }
}

rootProject.name = "compose-server"

includeBuild("gradle-plugin")

include("oss")

findProject(":oss")?.name = "oss"

include("core")

findProject(":core")?.name = "core"

include("rds")

findProject(":rds")?.name = "rds"

include("rds:rds-gen")

findProject(":rds:rds-gen")?.name = "rds-gen"

include("rds:rds-core")

findProject(":rds:rds-core")?.name = "rds-core"

include("data-common")

findProject(":data-common")?.name = "data-common"

include("data-common:data-common-crawler")

findProject(":data-common:data-common-crawler")?.name = "data-common-crawler"

include("data-common:data-common-data-extract")

findProject(":data-common:data-common-data-extract")?.name = "data-common-data-extract"

include("security")

findProject(":security")?.name = "security"

include("security:security-oauth2")

findProject(":security:security-oauth2")?.name = "security-oauth2"

include("web-api-doc")

findProject(":web-api-doc")?.name = "web-api-doc"

include("depend")

findProject(":depend")?.name = "depend"

include("depend:depend-web-servlet")

findProject(":depend:depend-web-servlet")?.name = "depend-web-servlet"

include("depend:depend-mqtt")

findProject(":depend:depend-mqtt")?.name = "depend-mqtt"

include("depend:depend-flyway")

findProject(":depend:depend-flyway")?.name = "depend-flyway"

include("depend:depend-web-client")

findProject(":depend:depend-web-client")?.name = "depend-web-client"

include("depend:depend-jvalid")

findProject(":depend:depend-jvalid")?.name = "depend-jvalid"

include("cacheable")

findProject(":depend:test-toolkit")?.name = "test-toolkit"

include("test-toolkit")

findProject(":cacheable")?.name = "cacheable"

include("schedule")

findProject(":schedule")?.name = "schedule"

include("pay")

findProject(":pay")?.name = "pay"

include("ksp")

findProject(":ksp")?.name = "ksp"

include("ksp:ksp-test")

findProject(":ksp:ksp-test")?.name =
  "ksp-test"

// include("version-check")
//
// findProject(":version-check")?.name = "version-check"
