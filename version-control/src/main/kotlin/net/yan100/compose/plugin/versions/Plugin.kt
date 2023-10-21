package net.yan100.compose.plugin.versions

import net.yan100.compose.plugin.V

object Plugin {
  // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-gradle-plugin/3.0.5
  const val spring = V.Spring.springBoot

  // https://mvnrepository.com/artifact/io.spring.gradle/dependency-management-plugin
  const val springDependencyManagement = "1.1.3"

  // https://mvnrepository.com/artifact/org.jetbrains.kotlin.plugin.spring/org.jetbrains.kotlin.plugin.spring.gradle.plugin
  const val kotlinSpring = V.Lang.kotlin

  // https://mvnrepository.com/artifact/org.jetbrains.kotlin.plugin.jpa/org.jetbrains.kotlin.plugin.jpa.gradle.plugin
  const val kotlinJpa = V.Lang.kotlin

  // https://mvnrepository.com/artifact/org.jetbrains.kotlin.plugin.lombok/org.jetbrains.kotlin.plugin.lombok.gradle.plugin
  const val kotlinLombok = V.Lang.kotlin

  // kotlin 注解处理器
  // https://mvnrepository.com/artifact/org.jetbrains.kotlin.kapt/org.jetbrains.kotlin.kapt.gradle.plugin
  const val kotlinKapt = V.Lang.kotlin

  // kotlin jvm 插件
  // https://mvnrepository.com/artifact/org.jetbrains.kotlin.jvm/org.jetbrains.kotlin.jvm.gradle.plugin
  const val kotlinJvmPlugin = V.Lang.kotlin

  // https://mvnrepository.com/artifact/com.github.ben-manes/gradle-versions-plugin
  const val versionManager = "0.49.0"

  /**
   * graalvm 可能需要的
   * @see [maven](https://mvnrepository.com/artifact/org.asciidoctor.jvm.convert/org.asciidoctor.jvm.convert.gradle.plugin?repo=gradle-plugins)
   */
  const val ascijvmConvert = "3.3.2"
}
