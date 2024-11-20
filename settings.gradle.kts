dependencyResolutionManagement { versionCatalogs { create("libs") { from(files("libs.versions.toml")) } } }

rootProject.name = "compose-server"
includeBuild("gradle-plugin")
includeBuild("version-catalog")

include("oss")
findProject(":oss")?.name = "oss"

include("core")
findProject(":core")?.name = "core"

include("test-toolkit")
findProject(":test-toolkit")?.name = "test-toolkit"

include("cacheable")
findProject(":cacheable")?.name = "cacheable"

include("schedule")
findProject(":schedule")?.name = "schedule"

include("pay")
findProject(":pay")?.name = "pay"


// 关系型数据库
include("rds:crud")
findProject(":rds:crud")?.name = "rds-crud"
include("rds:core")
findProject(":rds:core")?.name = "rds-core"
include("rds:jimmer")
findProject(":rds:jimmer")?.name = "rds-jimmer"
include("rds:migration-mysql")
findProject(":rds:migration-mysql")?.name = "rds-migration-mysql"
include("rds:migration-postgres")
findProject(":rds:migration-postgres")?.name = "rds-migration-postgres"

// 数据采集器
include("data")
findProject(":data")?.name = "data"
include("data:crawler")
findProject(":data:crawler")?.name = "data-crawler"
include("data:extract")
findProject(":data:extract")?.name = "data-extract"

// 安全相关
include("security:spring")
findProject(":security:spring")?.name = "security-spring"
include("security:oauth2")
findProject(":security:oauth2")?.name = "security-oauth2"
include("security:crypto")
findProject(":security:crypto")?.name = "security-crypto"


// 特定依赖处理
include("depend")
findProject(":depend")?.name = "depend"
include("depend:servlet")
findProject(":depend:servlet")?.name = "depend-servlet"
include("depend:paho")
findProject(":depend:paho")?.name = "depend-paho"
include("depend:http-exchange")
findProject(":depend:http-exchange")?.name = "depend-http-exchange"
include("depend:jsr303-validation")
findProject(":depend:jsr303-validation")?.name = "depend-jsr303-validation"
include("depend:jackson")
findProject(":depend:jackson")?.name = "depend-jackson"
include("depend:springdoc-openapi")
findProject(":depend:springdoc-openapi")?.name = "depend-springdoc-openapi"


// ksp
include("ksp:plugin")
findProject(":ksp:plugin")?.name = "ksp-plugin"
include("ksp:test")
findProject(":ksp:test")?.name = "ksp-test"
include("ksp:core")
findProject(":ksp:core")?.name = "ksp-core"
include("ksp:toolkit")
findProject(":ksp:toolkit")?.name = "ksp-toolkit"
