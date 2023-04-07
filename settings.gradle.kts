dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
}

pluginManagement {
  repositories {
    mavenLocal()
    maven("https://maven.aliyun.com/repository/gradle-plugin")
    mavenCentral()
    gradlePluginPortal()
    google()
  }

  resolutionStrategy {
    eachPlugin {
      when (requested.id.id) {
        "org.springframework.boot" -> {
          useVersion("3.0.5")
        }

        "io.spring.dependency-management" -> {
          useVersion("1.1.0")
        }
      }
    }
  }
}

rootProject.name = "component"

include("oss")
findProject(":oss")?.name = "oss"

include("core")
findProject(":core")?.name = "core"

include("rds")
findProject(":rds")?.name = "rds"

include("rds:rds-gen")
findProject("rds:rds-gen")?.name = "rds-gen"

include("data-common")
findProject(":data-common")?.name = "data-common"

include("data-common:data-common-crawler")
findProject("data-common:data-common-crawler")?.name = "data-common-crawler"

include("data-common:data-common-data-extract")
findProject("data-common:data-common-data-extract")?.name = "data-common-data-extract"

include("security")
findProject(":security")?.name = "security"

include("web-api-doc")
findProject(":web-api-doc")?.name = "web-api-doc"

include("depend")
findProject(":depend")?.name = "depend"

include("depend:depend-web-servlet")
findProject("depend:depend-web-servlet")?.name = "depend-web-servlet"

include("depend:depend-flyway")
findProject("depend:depend-flyway")?.name = "depend-flyway"

include("cacheable")
findProject(":cacheable")?.name = "cacheable"

include("schedule")
findProject(":schedule")?.name = "schedule"

include("multi-test")
findProject(":multi-test")?.name = "multi-test"

include("multi-test:multi-test-security")
findProject("multi-test:multi-test-security")?.name = "multi-test-security"
