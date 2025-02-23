import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

plugins {
  `maven-publish`
  signing
}

val yunxiaoUrl = extra["repositories.url.yunxiao"].toString()
val yunxiaoUsername = extra["repositories.username.yunxiao"].toString()
val yunxiaoPassword = extra["repositories.password.yunxiao"].toString()

publishing {
  repositories {
    maven(url = uri(yunxiaoUrl)) {
      credentials {
        username = yunxiaoUsername
        password = yunxiaoPassword
      }
    }
  }

  publications {
    create<MavenPublication>("mavenJava") {
      groupId = libs.versions.compose.group.get()
      artifactId = project.name
      when {
        plugins.hasPlugin("java-gradle-plugin") ||
          plugins.hasPlugin("java-library") ||
          plugins.hasPlugin("java") -> from(components["java"])

        plugins.hasPlugin("java-platform") -> from(components["javaPlatform"])

        plugins.hasPlugin("version-catalog") -> from(components["versionCatalog"])
        else -> throw IllegalStateException("Unknown plugin type")
      }
    }
  }

  afterEvaluate {
    publishing.publications?.withType<MavenPublication>()?.forEach { pub ->
      version = project.version.toString()
      pub.pom {
        name = "${rootProject.name}-${project.name}"
        description = project.description
        url = "https://github.com/TrueNine/compose-server"
        licenses {
          license {
            name = "The private license of TrueNine"
            url = "https://github.com/TrueNine/compose-server/blob/main/LICENSE"
          }
        }
        inceptionYear = "2020"
        developers {
          developer {
            id = "TrueNine"
            name = "赵日天"
            email = "truenine304520@gmail.com"
          }
          developer {
            id = "t_teng"
            name = "阿腾"
            email = "616057370@qq.com"
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
          url = "https://gitee.com/yan100"
        }
        issueManagement {
          system = "GitHub"
          url = "https://github.com/TrueNine/compose-server/issues"
        }
        properties = mapOf(
          "project.build.sourceEncoding" to "UTF-8",
          "maven.compiler.source" to libs.versions.java.get(),
          "maven.compiler.target" to libs.versions.java.get(),
          "maven.compiler.release" to libs.versions.java.get()
        )
      }
    }
  }
}

signing {
  useGpgCmd()
  sign(publishing.publications["mavenJava"])
}
