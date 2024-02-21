pluginManagement {
  val release = "https://packages.aliyun.com/maven/repository/2336368-release-CiFRF5/"
  val yunXiaoUsername: String = System.getenv("YUNXIAO_USER")
  val yunXiaoPassword: String = System.getenv("YUNXIAO_PWD")

  repositories {
    mavenLocal()
    maven(url = uri("https://repo.huaweicloud.com/repository/maven/"))
    maven(url = uri("https://repo.spring.io/milestone"))
    maven(url = uri(release)) {
      isAllowInsecureProtocol = true
      credentials {
        username = yunXiaoUsername
        password = yunXiaoPassword
      }
    }
    gradlePluginPortal()
    mavenCentral()
  }
}

plugins { id("net.yan100.compose.version-control-settings") version "1.6.314" }

dependencyResolutionManagement {
  versionCatalogs { create("libs") { from(files("version-control/libs.versions.toml")) } }
}

rootProject.name = "compose"

includeBuild("version-control")

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

include("cacheable")

findProject(":cacheable")?.name = "cacheable"

include("schedule")

findProject(":schedule")?.name = "schedule"

include("pay")

findProject(":pay")?.name = "pay"

include("ksp")

findProject(":ksp")?.name = "ksp"
