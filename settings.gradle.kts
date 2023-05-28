pluginManagement {
  repositories {
    mavenLocal()
    maven(url = uri("https://repo.huaweicloud.com/repository/maven/"))
    maven(url = uri("https://maven.aliyun.com/repository/gradle-plugin"))
    maven(url = uri("https://maven.aliyun.com/repository/jcenter"))
    maven(url = uri("https://maven.aliyun.com/repository/public"))
    mavenCentral()
    gradlePluginPortal()
    google()
  }
}

rootProject.name = "compose"

include("oss")
findProject(":oss")?.name = "oss"

include("core")
findProject(":core")?.name = "core"

include("rds")
findProject(":rds")?.name = "rds"

include("rds:rds-gen")
findProject(":rds:rds-gen")?.name = "rds-gen"

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

include("depend:depend-flyway")
findProject(":depend:depend-flyway")?.name = "depend-flyway"

include("depend:depend-web-client")
findProject(":depend:depend-web-client")?.name = "depend-web-client"


include("cacheable")
findProject(":cacheable")?.name = "cacheable"

include("schedule")
findProject(":schedule")?.name = "schedule"

include("multi-test")
findProject(":multi-test")?.name = "multi-test"

include("multi-test:multi-test-security")
findProject(":multi-test:multi-test-security")?.name = "multi-test-security"

include("multi-test:multi-test-pay")
findProject(":multi-test:multi-test-pay")?.name = "multi-test-pay"


include("pay")
findProject(":pay")?.name = "pay"
