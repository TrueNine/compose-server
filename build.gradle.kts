import Repos.Credentials.yunXiaoPassword
import Repos.Credentials.yunXiaoUsername
import Repos.release
import Repos.snapshot
import org.springframework.boot.gradle.tasks.aot.ProcessAot
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
  id("java")
  id("java-library")
  id("org.springframework.boot") version V.Spring.boot
  id("io.spring.dependency-management") version V.Spring.managementPlugin
  kotlin("jvm") version V.Lang.kotlin
  kotlin("kapt") version V.Lang.kotlin
  kotlin("plugin.spring") version V.Lang.kotlin
  kotlin("plugin.jpa") version V.Lang.kotlin
  kotlin("plugin.lombok") version V.Lang.kotlin
  id("maven-publish")
}


allprojects {
  repositories {
    maven(release) {
      this.isAllowInsecureProtocol = true
      credentials {
        this.username = yunXiaoUsername
        this.password = yunXiaoPassword
      }
    }
    maven(snapshot) {
      this.isAllowInsecureProtocol = true
      credentials {
        this.username = yunXiaoUsername
        this.password = yunXiaoPassword
      }
    }

    maven(Repos.aliCentral)
    maven(Repos.aliJCenter)
    maven(Repos.aliPublic)
    maven(Repos.aliGradlePlugin)
    maven(Repos.aliSpring)
    maven(Repos.aliApacheSnapshots)
    maven(Repos.spring1)
    maven(Repos.spring2)
    maven(Repos.spring3)
    mavenCentral()
    gradlePluginPortal()
    google()
  }

  tasks.withType<ProcessAot> {
    enabled = false
  }

//  tasks.withType<Javadoc> {
//    enabled = true
//    options {
//      this.encoding = "UTF-8"
//      this.locale = "zh-CN"
//    }
//  }

  tasks.withType<BootJar> {
    enabled = false
  }

  tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
      freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=all")
      jvmTarget = V.Lang.javaStr
    }
  }

  tasks.withType<AbstractCopyTask> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
  }

  tasks.withType<Test> {
    useTestNG()
  }
  group = ProjectManager.group
  version = ProjectManager.version
}

subprojects {
  apply(plugin = "java")
  apply(plugin = "org.jetbrains.kotlin.plugin.lombok")
  apply(plugin = "kotlin")
  apply(plugin = "org.springframework.boot")
  apply(plugin = "io.spring.dependency-management")
  apply(plugin = "java-library")
  apply(plugin = "maven-publish")
  java.sourceCompatibility = V.Lang.javaEnum

  java {
    withSourcesJar()
  }

  tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
      (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
  }

  publishing {
    repositories {
      maven(
        if (version.toString().endsWith("SNAPSHOT")) snapshot else release
      ) {
        credentials {
          this.username = yunXiaoUsername
          this.password = yunXiaoPassword
        }
      }
    }

    publications {
      create<MavenPublication>("maven") {
        groupId = project.group.toString()
        artifactId = project.name
        version = project.version.toString()
        from(components["java"])
      }
    }
  }

  dependencies {
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    api("org.jetbrains.kotlin:kotlin-reflect")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    api("io.projectreactor.kotlin:reactor-kotlin-extensions")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    compileOnly("org.springframework.cloud:spring-cloud-starter-bootstrap")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
      exclude("org.junit.jupiter", "junit-jupiter")
    }

    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-testng:${V.Test.kotlinTestNG}")
    testImplementation("org.testng:testng:${V.Test.testNG}")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
  }

  dependencyManagement {
    dependencies {
      // 自身版本管理
      dependency("${group}:core:${V.Project.core}")
      dependency("${group}:oss:${V.Project.oss}")
      dependency("${group}:security:${V.Project.security}")
      dependency("${group}:rds:${V.Project.rds}")
      dependency("${group}:rds-gen:${V.Project.rdsGen}")
      dependency("${group}:web-api-doc:${V.Project.webApiDoc}")
      dependency("${group}:web-servlet:${V.Project.webServlet}")
      dependency("${group}:crawler:${V.Project.crawler}")
      dependency("${group}:cacheable:${V.Project.cacheable}")
      dependency("${group}:schedule:${V.Project.schedule}")
      dependency("${group}:flyway:${V.Project.flyway}")
      dependency("${group}:data-extract:${V.Project.dataExtract}")

      // api
      dependency("jakarta.validation:jakarta.validation-api:${V.Api.jakartaValidation}")
      dependency("jakarta.servlet:jakarta.servlet-api:${V.Api.jakartaServlet}")
      // driver
      dependency("com.mysql:mysql-connector-j:${V.Driver.mysql}")
      dependency("mysql:mysql-connector-java:${V.Driver.mysql}")
      dependency("org.xerial:sqlite-jdbc:${V.Driver.sqlite}")
      dependency("io.minio:minio:${V.Driver.minio}")
      dependency("org.seleniumhq.selenium:selenium-java:${V.Driver.selenium}")
      dependency("io.github.bonigarcia:webdrivermanager:${V.Driver.webDriverManager}")
      dependency("org.hibernate:hibernate-entitymanager:${V.Driver.hibernateEntityManager}")
      dependency("org.hibernate.orm:hibernate-core:${V.Driver.hibernateCore}")
      dependency("p6spy:p6spy:${V.Driver.p6spy}")

      // sdk
      dependency("com.aliyun.oss:aliyun-sdk-oss:${V.Sdk.aliYunOss}")

      // hibernate
      dependency("com.vladmihalcea:hibernate-types-60:${V.Jpa.vladmihalceaHibernateTypes}")
      dependency("com.vladmihalcea:hibernate-types-52:${V.Jpa.vladmihalceaHibernateTypes}")

      // junit
      dependency("org.junit.jupiter:junit-jupiter-api:${V.Test.junit5}")
      dependency("org.junit.jupiter:junit-jupiter-engine:${V.Test.junit5}")

      // open api 文档
      dependency("org.springdoc:springdoc-openapi-ui:${V.OpenApi.springDoc1}")
      dependency("org.springdoc:springdoc-openapi-ui:${V.OpenApi.springDoc1}")
      dependency("org.springdoc:springdoc-openapi-common:${V.OpenApi.springDoc1}")
      dependency("org.springdoc:springdoc-openapi-webmvc-core:${V.OpenApi.springDoc1}")
      dependency("org.springdoc:springdoc-openapi-starter-webmvc-ui:${V.OpenApi.springDoc2}")
      dependency("com.github.xiaoymin:knife4j-springdoc-ui:${V.OpenApi.knife4j}")
      dependency("io.swagger.core.v3:swagger-annotations-jakarta:${V.OpenApi.swaggerAnnotation}")

      // 工具
      dependency("com.google.guava:guava:${V.Util.guava}")
      dependency("com.squareup.okhttp3:okhttp:${V.Http.okhttp3}")
      dependency("org.lionsoul:ip2region:${V.Util.ip2Region}")
      dependency("org.jsoup:jsoup:${V.Util.jsoup}")
      dependency("com.github.haifengl:smile-math:${V.Util.smileMath}")
      dependency("com.github.magese:ik-analyzer:${V.Util.ikAnalyzer}")
      // hutool
      dependency("cn.hutool:hutool-all:${V.Util.huTool}")
      dependency("cn.hutool:hutool-captcha:${V.Util.huTool}")
      dependency("cn.hutool:hutool-crypto:${V.Util.huTool}")
      dependency("cn.hutool:hutool-db:${V.Util.huTool}")
      // util
      dependency("ognl:ognl:${V.Util.ognl}")
      dependency("net.sf.dozer:dozer:${V.Util.dozer}")
      dependency("com.google.code.gson:gson:${V.Util.gson}")

      // 安全
      dependency("org.owasp.antisamy:antisamy:${V.Security.antisamy}")
      dependency("net.sourceforge.nekohtml:nekohtml:${V.Security.nekohtml}")
      dependency("org.bouncycastle:bcprov-jdk15to18:${V.Security.bouncyCastle15to18}")

      // jwt
      dependency("io.jsonwebtoken:jjwt:${V.Jwt.jJwt}")
      dependency("com.auth0:java-jwt:${V.Jwt.auth0Jwt}")

      // office
      dependency("org.freemarker:freemarker:${V.Template.freemarker}")
      dependency("com.alibaba:easyexcel:${V.Office.easyExcel}")
      dependency("org.flowable:flowable-spring-boot-starter:${V.Office.flowable}")
      dependency("org.flowable:flowable-spring-boot-starter-ui-modeler:${V.Office.flowable}")
      dependency("org.apache.commons:org.apache.commons:${V.Office.commonsCsv}")
      // xxl
      dependency("com.xuxueli:xxl-job-core:${V.Schedule.xxlJob}")
    }

    imports {
      mavenBom("org.springframework.boot:spring-boot-dependencies:${V.Spring.boot}")
      mavenBom("org.springframework.cloud:spring-cloud-dependencies:${V.Spring.cloud}")
      mavenBom("com.alibaba.cloud:spring-cloud-alibaba-dependencies:${V.Spring.cloudAlibaba}")
    }
  }

  configurations {
    compileOnly {
      extendsFrom(configurations.annotationProcessor.get())
    }
  }
}

tasks.wrapper {
  gradleVersion = V.Lang.gradleWrapper
}
