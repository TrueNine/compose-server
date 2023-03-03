/**
 * 项目顶层管理版本
 */
object ProjectManager {
  const val group = "com.truenine.component"
  const val version = "0.2.3"
  const val encoding = "UTF-8"
}

/**
 * 版本管理
 */
object V {
  object Project {
    private const val allV = ProjectManager.version
    const val core = allV
    const val rds = allV
    const val gen = allV
    const val webApiDoc = allV
    const val security = allV
    const val webServlet = allV
    const val crawler = allV
    const val cacheable = allV
    const val oss = allV
  }

  object Lang {
    const val gradleWrapper = "8.0"
    const val javaStr = "17"
    val javaEnum = org.gradle.api.JavaVersion.VERSION_17
    const val kotlin = "1.8.0"
  }

  object Driver {
    const val hibernateCore = Jpa.hibernate
    const val hibernateEntityManager = "6.0.0.Alpha7"
    const val mysql = "8.0.32"
    const val sqlite = "3.40.1.0"
    const val minio = "8.5.2"
    const val selenium = "4.8.0"
    const val webDriverManager = "5.3.2"
  }

  object Sdk {
    const val aliYunOss = "3.16.0"
  }

  object Jpa {
    const val vladmihalceaHibernateTypes = "2.20.0"
    const val hibernate = "6.1.7.Final"
  }

  object Office {
    const val flowable = "7.0.0.M1"
    const val easyExcel = "3.2.1"
  }

  object Util {
    const val jsoup = "1.15.3"
    const val ognl = "3.3.4"
    const val gson = "2.10.1"
    const val dozer = "6.5.2"
    const val huTool = "5.8.12"
    const val guava = "31.1-jre"
    const val ip2Region = "2.7.0"
    const val smileMath = "2.6.0"
    const val ikAnalyzer = "8.5.0"
  }

  object Http {
    const val okhttp3 = "5.0.0-alpha.10"
  }

  object Api {
    const val javaxServlet = "4.0.1"
    const val jakartaValidation = "3.0.2"
    const val jakartaServlet = "6.0.0"
  }

  object Template {
    const val freemarker = "2.3.31"
  }

  object Test {
    const val junit5 = "5.9.1"
    const val testNG = "7.7.0"
    const val kotlinTestNG = "1.6.21"
  }

  object OpenApi {
    const val springDoc2 = "2.0.0"
    const val springDoc1 = "1.6.13"
    const val knife4j = "3.0.3"
    const val swaggerAnnotation = "2.2.7"
  }

  object Spring {
    const val graalVmPlugin = "0.9.18"
    const val boot = "3.0.3"
    const val cloud = "2022.0.0-RC2"
    const val cloudAlibaba = "2021.0.4.0"
    const val managementPlugin = "1.1.0"
  }

  object Security {
    const val antisamy = "1.7.2"
  }

  object Jwt {
    const val auth0Jwt = "4.2.2"
    const val jJwt = "0.9.1"
  }
}

/**
 * maven 仓库管理
 */
object Repos {
  private const val repoAli = "https://maven.aliyun.com/"
  const val aliCentral = "${repoAli}repository/central"
  const val aliJCenter = "${repoAli}repository/jcenter"
  const val aliPublic = "${repoAli}repository/public"
  const val aliGoogle = "${repoAli}repository/google"
  const val aliGradlePlugin = "${repoAli}repository/gradle-plugin"
  const val aliSpring = "${repoAli}repository/spring"
  const val aliSpringPlugin = "${repoAli}repository/spring-plugin"
  const val aliGrailsCore = "${repoAli}repository/grails-core"
  const val aliApacheSnapshots = "${repoAli}repository/apache-snapshots"
  const val spring1 = "https://repo.spring.io/milestone"
  const val spring2 = "https://repo.spring.io/libs-milestone"
  const val spring3 = "https://repo.spring.io/snapshot"
}
