import Repos.Credentials.yunXiaoPassword
import Repos.Credentials.yunXiaoUsername
import Repos.release
import Repos.snapshot
import org.springframework.boot.gradle.tasks.aot.ProcessAot
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
  id("java")
  id("java-library")
  id("org.springframework.boot") version V.Spring.springBoot
  id("io.spring.dependency-management") version V.Plugin.dependencyManagementPlugin
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
  java.sourceCompatibility = V.Lang.javaPlatform

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
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${V.Lang.kotlin}")
    api("org.jetbrains.kotlin:kotlin-reflect:${V.Lang.kotlin}")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:${V.Lang.kotlinxCoroutine}")
    api("io.projectreactor.kotlin:reactor-kotlin-extensions:${V.Lang.reactorKotlinExtension}")


    compileOnly("org.springframework.cloud:spring-cloud-starter-bootstrap")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
      exclude("org.junit.jupiter", "junit-jupiter")
    }

    testApi("io.projectreactor:reactor-test")
    testApi("org.jetbrains.kotlin:kotlin-test-testng:${V.Test.kotlinTestNG}")
    testApi("org.testng:testng:${V.Test.testNG}")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
  }

  dependencyManagement {
    dependencies {
      dependency("${group}:core:${V.Component.core}")
      dependency("${group}:oss:${V.Component.oss}")
      dependency("${group}:security:${V.Component.security}")
      dependency("${group}:rds:${V.Component.rds}")
      dependency("${group}:rds-gen:${V.Component.rdsGen}")
      dependency("${group}:web-api-doc:${V.Component.webApiDoc}")
      dependency("${group}:data-common-crawler:${V.Component.dataCommonCrawler}")
      dependency("${group}:data-common-data-extract:${V.Component.dataCommonDataExtract}")
      dependency("${group}:cacheable:${V.Component.cacheable}")
      dependency("${group}:schedule:${V.Component.schedule}")
      dependency("${group}:depend-flyway:${V.Component.dependFlyway}")
      dependency("${group}:depend-web-servlet:${V.Component.dependWebServlet}")
      dependency("jakarta.validation:jakarta.validation-api:${V.StandardEdition.jakartaValidationApi}")
      dependency("jakarta.servlet:jakarta.servlet-api:${V.StandardEdition.jakartaServletApi}")
      dependency("com.mysql:mysql-connector-j:${V.Driver.mysqlConnectorJ}")
      dependency("org.xerial:sqlite-jdbc:${V.Driver.sqlite}")
      dependency("io.minio:minio:${V.PlatformSdk.minio}")
      dependency("org.seleniumhq.selenium:selenium-java:${V.Driver.seleniumJava}")
      dependency("io.github.bonigarcia:webdrivermanager:${V.Driver.webDriverManager}")
      dependency("org.hibernate.orm:hibernate-entitymanager:${V.Driver.hibernateCore}")
      dependency("org.hibernate.orm:hibernate-core:${V.Driver.hibernateCore}")
      dependency("p6spy:p6spy:${V.Driver.p6spy}")
      dependency("org.flywaydb:flyway-core:${V.Driver.flyway}")
      dependency("org.flywaydb:flyway-mysql:${V.Driver.flyway}")
      dependency("com.aliyun.oss:aliyun-sdk-oss:${V.PlatformSdk.aliYunOss}")
      dependency("org.junit.jupiter:junit-jupiter-api:${V.Test.junitJupiter}")
      dependency("org.junit.jupiter:junit-jupiter-engine:${V.Test.junitJupiter}")
      dependency("org.springdoc:springdoc-openapi-ui:${V.Web.springdocOpenapiUi}")
      dependency("org.springdoc:springdoc-openapi-ui:${V.Web.springdocOpenapiUi}")
      dependency("org.springdoc:springdoc-openapi-common:${V.Web.springdocOpenapiUi}")
      dependency("org.springdoc:springdoc-openapi-webmvc-core:${V.Web.springdocOpenapiUi}")
      dependency("org.springdoc:springdoc-openapi-starter-webmvc-ui:${V.Web.springdocOpenapiWebmvcUi}")
      dependency("com.github.xiaoymin:knife4j-springdoc-ui:${V.Web.knife4j}")
      dependency("io.swagger.core.v3:swagger-annotations-jakarta:${V.StandardEdition.swaggerAnnotationJakarta}")
      dependency("com.google.guava:guava:${V.Util.guava}")
      dependency("com.squareup.okhttp3:okhttp:${V.Web.okhttp3}")
      dependency("org.lionsoul:ip2region:${V.Util.ip2Region}")
      dependency("org.jsoup:jsoup:${V.Util.jsoup}")
      dependency("com.github.haifengl:smile-math:${V.Util.smileMath}")
      dependency("com.github.magese:ik-analyzer:${V.Util.ikAnalyzer}")
      dependency("cn.hutool:hutool-all:${V.Util.huTool}")
      dependency("cn.hutool:hutool-captcha:${V.Util.huTool}")
      dependency("cn.hutool:hutool-crypto:${V.Util.huTool}")
      dependency("cn.hutool:hutool-db:${V.Util.huTool}")
      dependency("ognl:ognl:${V.Util.ognl}")
      dependency("net.sf.dozer:dozer:${V.Util.dozer}")
      dependency("com.google.code.gson:gson:${V.Util.gson}")
      dependency("org.owasp.antisamy:antisamy:${V.Security.antisamy}")
      dependency("net.sourceforge.nekohtml:nekohtml:${V.Security.nekohtml}")
      dependency("org.bouncycastle:bcprov-jdk15to18:${V.Security.bcprovJdk15to18}")
      dependency("io.jsonwebtoken:jjwt:${V.Jwt.jJwt}")
      dependency("com.auth0:java-jwt:${V.Jwt.auth0JavaJwt}")
      dependency("org.freemarker:freemarker:${V.Util.freemarker}")
      dependency("com.alibaba:easyexcel:${V.Util.easyExcel}")
      dependency("org.flowable:flowable-spring-boot-starter:${V.PlatformSdk.flowable}")
      dependency("org.flowable:flowable-spring-boot-starter-ui-modeler:${V.PlatformSdk.flowable}")
      dependency("net.sf.supercsv:super-csv:${V.Util.superCsv}")
    }

    imports {
      mavenBom("org.springframework.boot:spring-boot-dependencies:${V.Spring.springBoot}")
      mavenBom("org.springframework.cloud:spring-cloud-dependencies:${V.Spring.springCloud}")
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
