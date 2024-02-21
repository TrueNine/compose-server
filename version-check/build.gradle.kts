project.version = libs.versions.compose.asProvider().get()
plugins {
  alias(libs.plugins.spotless)
  alias(libs.plugins.ktJvm)
  alias(libs.plugins.ktKapt)
  alias(libs.plugins.ktSpring)
  alias(libs.plugins.ktAllOpen)
  alias(libs.plugins.ktLombok)
  alias(libs.plugins.ktJpa)
  alias(libs.plugins.springBoot)
  alias(libs.plugins.springBootDependencyManagement)
  alias(libs.plugins.hibernateOrm)
  alias(libs.plugins.versions)
  alias(libs.plugins.graalvmNative)
  alias(libs.plugins.asciJvmConvert)
}
val l = libs
dependencies {
  // bundles
  testApi(l.bundles.test.springKotlinTestNG)
  testApi(l.bundles.test.springKotlinJunit5)
  api(l.bundles.kt)
  testApi(l.bundles.p6spySpring)
  api(l.bundles.spring.redis)
  api(l.bundles.spring.jpa)
  api(l.bundles.spring.jpa)
  api(l.bundles.selenium)
  api(l.bundles.flyway)
  api(l.bundles.knife4j)

  // libraries
  // boot
  api(l.spring.boot.autoconfigure)
  annotationProcessor(l.spring.boot.configureprocessor)
  testApi(l.spring.boot.test)
  api(l.spring.boot.json)
  api(l.spring.boot.webflux)
  api(l.spring.boot.web)
  api(l.spring.boot.actuator)
  api(l.spring.boot.websocket)
  api(l.spring.boot.validation)
  api(l.spring.boot.security)
  api(l.spring.boot.undertow)
  api(l.spring.boot.tomcat)
  api(l.spring.boot.dataRedis)
  api(l.spring.boot.dataJpa)
  api(l.spring.boot.dataRedisReactive)
  api(l.spring.boot.logging)
  api(l.spring.boot.dockerCompose)

  // cloud
  api(l.spring.cloud.bootstrap)
  // integration
  api(l.spring.integration.mqtt)
  // data
  api(l.spring.data.springDataCommons)
  //security
  testApi(l.spring.security.test)
  api(l.spring.security.crypto)
  api(l.spring.security.core)
  // spring
  api(l.spring.webmvc)
  // modulith
  api(l.spring.modulith.jpa)
  api(l.spring.modulith.core)
  testApi(l.spring.modulith.test)
  // doc
  api(l.spring.doc.webmvcUi3)

  // apache
  api(l.apache.commonsPool2)
  api(l.apache.commonsCompress)

  // kt
  api(l.kt.kspGoogleApi)
  api(l.kt.stdlib)
  api(l.kt.stdlibJdk8)
  api(l.kt.reflact)
  testApi(l.kt.testng)
  testApi(l.kt.junit5)
  api(l.kt.reactorExtensions)
  api(l.ktx.coroutineReactor)
  api(l.jetbrains.annotation)

  // test
  testApi(l.test.mockk)
  testApi(l.test.testng)
  testApi(l.test.junit5)
  testApi(l.test.reactorTest)

  // cache
  api(l.cache.caffeine)

  // db
  api(l.db.hsql)
  api(l.db.h2)
  api(l.db.postgresql)
  api(l.db.sqlite)
  api(l.db.mysqlJ)
  api(l.db.mysqJava)
  api(l.db.flywayCore)
  api(l.db.flywayMysql)
  api(l.db.p6spy)
  api(l.db.p6spySpring)

  // sdk
  // oss
  api(l.sdk.oss.minio)
  api(l.sdk.oss.aliyun)
  api(l.sdk.oss.huaweicloud)
  // pay
  api(l.sdk.pay.wechatv3)
  // openapi
  api(l.sdk.openapi.knife4j)
  api(l.sdk.openapi.knife4jJakarta)

  // jakarta
  api(l.jakarta.annotationApi)
  api(l.jakarta.servletApi)
  api(l.jakarta.persistenceApi)
  api(l.jakarta.validationApi)
  api(l.jakarta.openapiV3Annotations)

  // net
  api(l.net.okhttp3)
  api(l.net.retrofig)

  // util
  api(l.util.byteBuddy)
  api(l.util.guavaJre)
  api(l.util.guavaAndroid)
  api(l.util.ognl)
  api(l.util.ikanalyzer)
  api(l.util.smilemath)
  api(l.util.easyexcel)
  api(l.util.freemarker)
  api(l.util.dozer)
  // hutool
  api(l.util.hutoolAll)
  api(l.util.hutoolCore)
  api(l.util.hutoolCaptcha)
  api(l.util.hutoolCrypto)
  api(l.util.hutoolDb)
  api(l.util.mapstruct)
  api(l.util.mapstructProcessor)

  //  security
  api(l.security.jjwtApi)
  api(l.security.bcprovJdk18on)
  api(l.security.auth0Jwt)
  api(l.security.antisamy)

  // slf4j
  api(l.slf4j.api)

  // crawler
  api(l.crawler.ip2region)
  api(l.crawler.selenium)
  api(l.crawler.seleniumWebDriverManager)
  api(l.crawler.playwright)
  api(l.crawler.jsoup)
  api(l.crawler.nekohtml)
  api(l.crawler.supercsv)

  // json
  api(l.json.jacksonModuleKotlin)
  api(l.json.jacksonCoreAnnotations)
  api(l.json.gson)

  // drools
  api(l.drools.core)
  api(l.drools.compiler)
  api(l.drools.decisiontables)
  api(l.drools.templates)
  api(l.drools.ruleunitsEngine)
  api(l.drools.modelCompiler)
  api(l.drools.engine)
  api(l.kie.api)
  api(l.kie.ci)
  api(l.kie.spring)

  // mybatis-plus
  api(l.mybatis.plus)
  annotationProcessor(l.lombok)

  // querydsl
  annotationProcessor(l.querydsl.jpa)
  annotationProcessor(l.querydsl.apt)


}
