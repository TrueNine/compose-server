/**
 * 项目顶层管理版本
 */
object ProjectManager {
  const val group = "com.truenine.component"
  const val version = "0.4.8-SNAPSHOT"
  const val encoding = "UTF-8"
}

/**
 * 版本管理
 */
object V {
  object Component {
    private const val allV = ProjectManager.version
    const val dataCommonDataExtract = allV
    const val dependFlyway = allV
    const val core = allV
    const val rds = allV
    const val schedule = allV
    const val rdsGen = allV
    const val webApiDoc = allV
    const val security = allV
    const val dependWebServlet = allV
    const val dataCommonCrawler = allV
    const val cacheable = allV
    const val oss = allV
    const val pay = allV
  }

  object Lang {
    const val kotlin = "1.8.20"
    const val gradleWrapper = "8.0.2"
    const val javaStr = "17"
    val javaPlatform = org.gradle.api.JavaVersion.VERSION_17

    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-reactor
    const val kotlinxCoroutine = "1.6.4"

    // https://mvnrepository.com/artifact/io.projectreactor.kotlin/reactor-kotlin-extensions
    const val reactorKotlinExtension = "1.2.2"

    // https://mvnrepository.com/artifact/org.jetbrains/annotations
    const val jetbrainsAnnotations = "24.0.1"
  }

  object Driver {
    // https://mvnrepository.com/artifact/p6spy/p6spy
    const val p6spy = "3.9.1"

    // https://mvnrepository.com/artifact/com.github.gavlyukovskiy/p6spy-spring-boot-starter
    const val p6spySpringBootStarter = "1.9.0"

    // https://mvnrepository.com/artifact/com.mysql/mysql-connector-j
    const val mysqlConnectorJ = "8.0.32"

    // https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc
    const val sqlite = "3.41.2.0"

    // https://mvnrepository.com/artifact/org.seleniumhq.selenium/selenium-java
    const val seleniumJava = "4.8.1"

    // https://mvnrepository.com/artifact/io.github.bonigarcia/webdrivermanager
    const val webDriverManager = "5.3.2"

    // https://mvnrepository.com/artifact/org.flywaydb/flyway-core
    // https://mvnrepository.com/artifact/org.flywaydb/flyway-mysql
    const val flyway = "9.16.0"

    // 测试用内存数据库
    // https://mvnrepository.com/artifact/com.h2database/h2
    const val h2 = "2.1.214"

    // https://mvnrepository.com/artifact/org.hsqldb/hsqldb
    const val hsqldb = "2.7.1"


    // https://mvnrepository.com/artifact/org.hibernate.orm/hibernate-core
    const val hibernateCore = "6.2.0.Final"

    // flowable 工作流引擎
    // https://mvnrepository.com/artifact/org.flowable/flowable-spring-boot-starter
    const val flowable = "7.0.0.M1"
  }

  object PlatformSdk {
    // 阿里云 oss sdk
    // https://mvnrepository.com/artifact/com.aliyun.oss/aliyun-sdk-oss
    const val aliYunOss = "3.16.2"

    // minio
    // https://mvnrepository.com/artifact/io.minio/minio
    const val minio = "8.5.2"

    // 微信支付 sdk
    // https://mvnrepository.com/artifact/com.github.wechatpay-apiv3/wechatpay-java
    const val wechatpayJava = "0.2.7"
  }

  object Util {
    // https://mvnrepository.com/artifact/com.alibaba/easyexcel
    const val easyExcel = "3.2.1"

    // csv 提取工具
    // https://mvnrepository.com/artifact/net.sf.supercsv/super-csv
    const val superCsv = "2.4.0"

    // https://mvnrepository.com/artifact/org.jsoup/jsoup
    const val jsoup = "1.15.4"

    // https://mvnrepository.com/artifact/ognl/ognl
    const val ognl = "3.3.4"

    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    const val gson = "2.10.1"

    // https://mvnrepository.com/artifact/net.sf.dozer/dozer
    const val dozer = "5.5.1"

    // https://mvnrepository.com/artifact/cn.hutool/hutool-all
    const val huTool = "5.8.16"

    // https://mvnrepository.com/artifact/com.google.guava/guava
    const val guava = "31.1-jre"

    // 离线ip包
    // https://mvnrepository.com/artifact/org.lionsoul/ip2region
    const val ip2Region = "2.7.0"

    // 用于计算海明距离
    // https://mvnrepository.com/artifact/com.github.haifengl/smile-math
    const val smileMath = "2.6.0"

    // 分词器，用于爬虫框架
    // https://mvnrepository.com/artifact/com.github.magese/ik-analyzer
    const val ikAnalyzer = "8.5.0"

    // 用于顶替 easyexcel
    // https://mvnrepository.com/artifact/org.apache.commons/commons-compress
    const val commonsCompress = "1.23.0"

    // 用于代码生成器
    // https://mvnrepository.com/artifact/org.freemarker/freemarker
    const val freemarker = "2.3.32"
  }

  object Web {
    // 用于发送网络请求
    // https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
    const val okhttp3 = "5.0.0-alpha.11"

    // https://mvnrepository.com/artifact/org.springdoc/springdoc-openapi-starter-webmvc-ui
    const val springdocOpenapiWebmvcUi = "2.1.0"

    // https://mvnrepository.com/artifact/org.springdoc/springdoc-openapi-ui
    const val springdocOpenapiUi = "1.7.0"

    // https://mvnrepository.com/artifact/com.github.xiaoymin/knife4j-spring-boot-starter
    const val knife4j = "3.0.3"

    // auth0 JWT
    // https://mvnrepository.com/artifact/com.auth0/java-jwt
    const val auth0JavaJwt = "4.4.0"

    // https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt
    const val jJwt = "0.9.1"
  }

  object StandardEdition {
    // https://mvnrepository.com/artifact/io.swagger.core.v3/swagger-annotations-jakarta
    const val swaggerAnnotationJakarta = "2.2.8"

    // https://mvnrepository.com/artifact/jakarta.validation/jakarta.validation-api
    const val jakartaValidationApi = "3.0.2"

    // https://mvnrepository.com/artifact/jakarta.persistence/jakarta.persistence-api
    const val jakartPersistenceApi = "3.1.0"

    // https://mvnrepository.com/artifact/jakarta.servlet/jakarta.servlet-api
    const val jakartaServletApi = "6.0.0"
  }

  object Schedule {
    // https://mvnrepository.com/artifact/com.xuxueli/xxl-job-core
    const val xxlJobCore = "2.3.1"
  }

  object Test {
    // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api
    // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-engine
    const val junitJupiter = "5.9.2"

    // https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-test-testng
    const val kotlinTestNG = "1.8.20"

    // https://mvnrepository.com/artifact/org.testng/testng
    const val testNG = "7.7.1"

    // kotlin mock k
    // https://mvnrepository.com/artifact/io.mockk/mockk
    const val mockk = "1.13.4"
  }

  object Spring {
    // https://spring.io/projects/spring-boot#learn
    const val springBoot = "3.0.5"

    // https://spring.io/projects/spring-cloud#learn
    const val springCloud = "2022.0.2"

    // https://spring.io/projects/spring-cloud-alibaba#learn
    const val cloudAlibaba = "2021.0.4.0"
  }

  object Plugin {
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-gradle-plugin/3.0.5
    const val spring = Spring.springBoot

    // https://mvnrepository.com/artifact/io.spring.gradle/dependency-management-plugin
    const val springDependencyManagement = "1.1.0"

    // https://mvnrepository.com/artifact/org.jetbrains.kotlin.plugin.spring/org.jetbrains.kotlin.plugin.spring.gradle.plugin
    const val kotlinSpring = Lang.kotlin

    // https://mvnrepository.com/artifact/org.jetbrains.kotlin.plugin.jpa/org.jetbrains.kotlin.plugin.jpa.gradle.plugin
    const val kotlinJpa = Lang.kotlin

    // https://mvnrepository.com/artifact/org.jetbrains.kotlin.plugin.lombok/org.jetbrains.kotlin.plugin.lombok.gradle.plugin
    const val kotlinLombok = Lang.kotlin

    // kotlin 注解处理器
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin.kapt/org.jetbrains.kotlin.kapt.gradle.plugin
    const val kotlinKapt = V.Lang.kotlin

    // kotlin jvm 插件
    // https://mvnrepository.com/artifact/org.jetbrains.kotlin.jvm/org.jetbrains.kotlin.jvm.gradle.plugin
    const val kotlinJvmPlugin = Lang.kotlin
  }

  object Security {
    // https://mvnrepository.com/artifact/org.owasp.antisamy/antisamy
    const val antisamy = "1.7.2"

    // 顶替 antisamy
    // https://mvnrepository.com/artifact/net.sourceforge.nekohtml/nekohtml
    const val nekohtml = "1.9.22"

    // 加密工具包
    // https://mvnrepository.com/artifact/org.bouncycastle/bcprov-jdk15to18
    const val bcprovJdk15to18 = "1.72"
  }
}

/**
 * maven 仓库管理
 */
object Repos {
  object Credentials {
    val yunXiaoUsername: String = System.getenv("YUNXIAO_USER")
    val yunXiaoPassword: String = System.getenv("YUNXIAO_PWD")
  }

  const val release =
    "https://packages.aliyun.com/maven/repository/2336368-release-CiFRF5/"
  const val snapshot =
    "https://packages.aliyun.com/maven/repository/2336368-snapshot-7SUFMh/"
  private const val repoAli = "https://maven.aliyun.com/"
  const val aliCentral = """${repoAli}repository/central"""
  const val aliJCenter = "${repoAli}repository/jcenter"
  const val aliPublic = "${repoAli}repository/public"
  const val aliGoogle = "${repoAli}repository/google"
  const val aliGradlePlugin = "${repoAli}repository/gradle-plugin"
  const val aliSpring = "${repoAli}repository/spring"
  const val aliSpringPlugin = "${repoAli}repository/spring-plugin"
  const val aliGrailsCore = "${repoAli}repository/grails-core"
  const val aliApacheSnapshots = "${repoAli}repository/apache-snapshots"
  const val springMilestone = "https://repo.spring.io/milestone"
  const val springLibMilestone = "https://repo.spring.io/libs-milestone"
  const val springSnapshot = "https://repo.spring.io/snapshot"
}
