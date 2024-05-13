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
  api(l.bundles.kt)
  api(l.bundles.p6spySpring)
  api(l.bundles.spring.redis)
  api(l.bundles.spring.jpa)
  api(l.bundles.spring.jpa)
  api(l.bundles.selenium)
  api(l.bundles.knife4j)

  // libraries
  // boot
  api(l.spring.boot.autoconfigure)
  annotationProcessor(l.spring.boot.configureprocessor)
  api(l.spring.boot.test)
  api(l.org.springframework.boot.spring.boot.starter.json)
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
  // security
  api(l.spring.security.test)
  api(l.spring.security.crypto)
  api(l.spring.security.core)
  // spring
  api(l.spring.webmvc)
  // modulith
  api(l.spring.modulith.jpa)
  api(l.spring.modulith.core)
  api(l.spring.modulith.test)
  // doc
  api(l.org.springdoc.springdoc.openapi.starter.webmvc.ui)

  // apache
  api(l.apache.commonsPool2)
  api(l.apache.commonsCompress)

  // kt
  api(l.kt.kspGoogleApi)
  api(l.kt.stdlib)
  api(l.kt.stdlibJdk8)
  api(l.kt.reflact)
  api(l.kt.reactorExtensions)
  api(l.ktx.coroutineReactor)
  api(l.jetbrains.annotation)

  // test
  api(l.io.mockk.mockk)
  api(l.test.testng)
  api(l.test.junit5)
  api(l.test.reactorTest)

  // cache
  api(l.com.github.ben.manes.caffeine.caffeine)

  // db
  api(l.org.hsqldb.hsqldb)
  api(l.com.h2database.h2)
  api(l.db.postgresql)
  api(l.org.xerial.sqlite.jdbc)
  api(l.db.mysqlJ)
  api(l.db.mysqJava)
  api(l.org.flywaydb.flyway.core)
  api(l.p6spy.p6spy)
  api(l.com.github.gavlyukovskiy.p6spy.spring.boot.starter)

  // sdk
  // oss
  api(l.io.minio.minio)
  api(l.sdk.oss.aliyun)
  api(l.io.minio.minio)
  // pay
  api(l.sdk.pay.wechatv3)
  // openapi
  api(l.sdk.openapi.knife4j)
  api(l.sdk.openapi.knife4jJakarta)

  // jakarta
  api(l.jakarta.annotation.jakarta.annotation.api)
  api(l.jakarta.servletApi)
  api(l.jakarta.persistenceApi)
  api(l.jakarta.validationApi)
  api(l.io.swagger.core.v3.swagger.annotations.jakarta)

  // net
  api(l.net.okhttp3)
  api(l.net.retrofig)

  // util
  api(l.util.byteBuddy)
  api(l.util.guavaJre)
  api(l.util.guavaAndroid)
  api(l.ognl.ognl)
  api(l.util.ikanalyzer)
  api(l.util.smilemath)
  api(l.util.easyexcel)
  api(l.util.freemarker)
  api(l.util.dozer)
  // hutool
  api(l.cn.hutool.hutool.all)
  api(l.org.mapstruct.mapstruct.asProvider())
  api(l.org.mapstruct.mapstruct.processor)

  //  security
  api(l.security.jjwtApi)
  api(l.org.bouncycastle.bcprov.jdk18on)
  api(l.security.auth0Jwt)
  api(l.security.antisamy)

  // slf4j
  api(l.org.slf4j.slf4j.api)

  // crawler
  api(l.crawler.ip2region)
  api(l.crawler.selenium)
  api(l.crawler.seleniumWebDriverManager)
  api(l.com.microsoft.playwright.playwright)
  api(l.crawler.jsoup)
  api(l.crawler.nekohtml)
  api(l.crawler.supercsv)

  // json
  api(l.json.jacksonModuleKotlin)
  api(l.json.jacksonCoreAnnotations)
  api(l.com.google.code.gson.gson)

  // drools
  api(l.org.drools.drools.core)
  api(l.org.drools.drools.compiler)
  api(l.org.drools.drools.decisiontables)
  api(l.org.drools.drools.templates)
  api(l.org.drools.drools.ruleunitsEngine)
  api(l.org.drools.drools.model.compiler)
  api(l.org.drools.drools.engine)
  api(l.org.kie.kie.api)
  api(l.org.kie.kie.ci)
  api(l.org.kie.kie.spring)

  api(l.com.yomahub.liteflow.spring.boot.starter)

  // mybatis-plus
  api(l.com.baomidou.mybatis.plus.boot.starter)
  annotationProcessor(l.org.projectlombok.lombok)

  // querydsl
  annotationProcessor(l.com.querydsl.querydsl.jpa)
  annotationProcessor(l.com.querydsl.querydsl.apt)
}
