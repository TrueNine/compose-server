import com.vanniktech.maven.publish.GradlePlugin
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import com.vanniktech.maven.publish.VersionCatalog

val libs = the<org.gradle.accessors.dm.LibrariesForLibs>()

plugins {
  // https://github.com/vanniktech/gradle-maven-publish-plugin
  id("com.vanniktech.maven.publish")
}

mavenPublishing {
  publishToMavenCentral(automaticRelease = true)
  signAllPublications()

  // 根据项目类型配置发布方式
  when {
    // 优先检测 version-catalog 插件
    project.plugins.hasPlugin("version-catalog") -> {
      configure(VersionCatalog())
    }
    // 检测 gradle plugin 相关插件
    project.plugins.hasPlugin("java-gradle-plugin") ||
      project.plugins.hasPlugin("kotlin-dsl") -> {
      configure(
        GradlePlugin(
          javadocJar = JavadocJar.Empty(),
          sourcesJar = true
        )
      )
    }
    // 检测 Kotlin JVM 插件（包括间接应用）
    project.plugins.hasPlugin("org.jetbrains.kotlin.jvm") ||
      project.plugins.hasPlugin("buildlogic.kotlin-conventions") ||
      project.plugins.hasPlugin("buildlogic.kotlinspring-conventions") -> {
      configure(
        KotlinJvm(
          javadocJar = JavadocJar.Empty(),
          sourcesJar = true
        )
      )
    }
  }

  coordinates(
    groupId = libs.versions.group.get(),
    artifactId = "composeserver-${project.name}",
    version = libs.versions.project.get()
  )
  pom {
    name = "${rootProject.name}-${project.name}"
    description = project.description ?: "A component of the ${rootProject.name} project"
    url = "https://github.com/TrueNine/compose-server"

    licenses {
      license {
        name = "GNU Lesser General Public License v2.1"
        url = "https://github.com/TrueNine/compose-server/blob/main/LICENSE"
      }
    }

    inceptionYear = "2020"

    developers {

      developer {
        id = "TrueNine"
        name = "赵日天"
        url = "https://github.com/TrueNine"
        organizationUrl = "https://github.com/TrueNine"
        timezone = "GMT+8"
        organization = "Yan100 Dev Group"
        roles = listOf("Developer", "Founder")
        email = "truenine304520@gmail.com"
      }
      developer {
        id = "t_teng"
        name = "滕腾"
        organization = "Nanning, Guangxi, China Yan100 technology Ltd"
        roles = listOf("Sponsor", "Founder")
        timezone = "GMT+8"
        email = "616057370@qq.com"
      }
      developer {
        id = "bambuo"
        name = "Johana Ĉen"
        roles = listOf("Sponsor")
        url = "https://github.com/bambuo"
        organization = "Hubei, China Tianshu technology Ltd"
        timezone = "GMT+8"
        email = "845586878@qq.com"
      }
      developer {
        id = "muyu"
        email = "1065700104@qq.com"
        roles = listOf("Sponsor")
        timezone = "GMT+8"
      }
    }

    scm {
      connection = "scm:git:git://github.com/TrueNine/compose-server.git"
      developerConnection = "scm:git:ssh://github.com:/TrueNine/compose-server.git"
      url = "https://github.com/TrueNine/compose-server"
      tag = project.version.toString()
    }

    organization {
      name = "Yan100 Dev Group"
      url = "https://github.com/TrueNine"
    }

    issueManagement {
      system = "GitHub"
      url = "https://github.com/TrueNine/compose-server/issues"
    }
    properties = mutableMapOf("project.build.sourceEncoding" to "UTF-8").apply {
      // 只有在项目应用了 Java 插件且配置了 toolchain 时才设置 Java 版本相关属性
      extensions.findByType<JavaPluginExtension>()?.toolchain?.languageVersion?.orNull?.asInt()?.toString()?.let { javaVersion ->
        put("java.version", javaVersion)
        put("maven.compiler.source", javaVersion)
        put("maven.compiler.target", javaVersion)
        put("maven.compiler.release", javaVersion)
      }
    }
  }
}

