import org.gradle.accessors.dm.LibrariesForLibs
import java.net.HttpURLConnection
import java.net.URI

val libs = the<LibrariesForLibs>()

plugins {
  `maven-publish`
  signing
}

// 仓库凭证
val repoConfig = mapOf(
  "yunxiao" to mapOf(
    "url" to extra["repositories.url.yunxiao"]?.toString(),
    "username" to extra["repositories.username.yunxiao"]?.toString(),
    "password" to extra["repositories.password.yunxiao"]?.toString()
  ), "yz-yunxiao" to mapOf(
    "url" to extra["repositories.url.yz-yunxiao"]?.toString(),
    "username" to extra["repositories.username.yz-yunxiao"]?.toString(),
    "password" to extra["repositories.password.yz-yunxiao"]?.toString()
  )
)

// 检查远程仓库中是否已存在制品
fun artifactExists(repoUrl: String, username: String?, password: String?, groupId: String, artifactId: String, version: String, extension: String): Boolean {
  val groupPath = groupId.replace('.', '/')
  val artifactUrl = "$repoUrl/$groupPath/$artifactId/$version/$artifactId-$version.$extension"

  return try {
    val url = URI(artifactUrl).toURL()
    val connection = url.openConnection() as HttpURLConnection

    if (!username.isNullOrBlank() && !password.isNullOrBlank()) {
      val auth = "Basic " + java.util.Base64.getEncoder().encodeToString("$username:$password".toByteArray())
      connection.setRequestProperty("Authorization", auth)
    }

    connection.requestMethod = "HEAD"
    connection.responseCode == HttpURLConnection.HTTP_OK
  } catch (e: Exception) {
    logger.debug("检查制品存在时出错: ${e.message}")
    false
  }
}

publishing {
  repositories {
    // 配置仓库
    repoConfig.forEach { (key, config) ->
      config["url"]?.also {
        maven(url = uri(it)) {
          credentials {
            username = config["username"]
            password = config["password"]
          }
        }
      }
    }
  }

  publications {
    create<MavenPublication>("mavenJava") {
      groupId = libs.versions.group.get()
      artifactId = project.name

      // 根据插件类型选择组件
      when {
        plugins.hasPlugin("version-catalog") -> from(components["versionCatalog"])
        plugins.hasPlugin("java-gradle-plugin") || plugins.hasPlugin("java-library") || plugins.hasPlugin("java") -> from(components["java"])
        plugins.hasPlugin("java-platform") -> from(components["javaPlatform"])
        else -> error("不支持的插件类型")
      }
    }
  }

  afterEvaluate {
    publishing.publications.withType<MavenPublication>().forEach { pub ->
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
            url = "https://github.com/TrueNine"
            timezone = "GMT+8"
            email = "truenine304520@gmail.com"
          }
          developer {
            id = "t_teng"
            name = "滕腾"
            timezone = "GMT+8"
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

        // 配置编译属性
        val javaVersion = extensions.findByType<JavaPluginExtension>()?.toolchain?.languageVersion?.get()?.asInt()?.toString()
        properties = mutableMapOf("project.build.sourceEncoding" to "UTF-8").apply {
          javaVersion?.let {
            put("java.version", it)
            put("maven.compiler.source", it)
            put("maven.compiler.target", it)
            put("maven.compiler.release", it)
          }
        }
      }
    }
  }
}

// 配置签名
signing {
  useGpgCmd()
  sign(publishing.publications["mavenJava"])
}

// 为每个发布任务添加检查逻辑，避免重复发布
afterEvaluate {
  tasks.withType<PublishToMavenRepository>().configureEach {
    doFirst {
      val publication = publication as MavenPublication
      val repoUrl = repository.url.toString()

      // 确定仓库凭据
      val repoType = when (repoUrl) {
        repoConfig["yunxiao"]?.get("url") -> "yunxiao"
        repoConfig["yz-yunxiao"]?.get("url") -> "yz-yunxiao"
        else -> null
      }

      val username = repoType?.let { repoConfig[it]?.get("username") }
      val password = repoType?.let { repoConfig[it]?.get("password") }

      // 检查主要制品是否已存在
      val extension = when {
        publication.artifacts.any { it.extension == "pom" } -> "pom"
        publication.artifacts.any { it.extension == "toml" } -> "toml"
        publication.artifacts.any { it.extension == "jar" } -> "jar"
        publication.artifacts.isEmpty() -> "pom"
        else -> "jar"
      }

      if (artifactExists(repoUrl, username, password, publication.groupId, publication.artifactId, publication.version, extension)) {
        logger.lifecycle("跳过发布 ${publication.groupId}:${publication.artifactId}:${publication.version} 到 $repoUrl，制品已存在")
        throw StopExecutionException("制品已存在，发布被跳过")
      }
    }
  }
}
