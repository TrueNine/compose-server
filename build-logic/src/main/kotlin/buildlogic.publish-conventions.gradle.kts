import java.net.HttpURLConnection
import java.net.URI

val libs = the<org.gradle.accessors.dm.LibrariesForLibs>()

plugins {
  `maven-publish`
  signing
}

val yunXiaoUsernameAndPassword = extra.properties["credentials.yunxiao.username"]?.toString() to extra.properties["credentials.yunxiao.password"]?.toString()
val yunXiaoRepositoryUrls = listOf(
  extra.properties["repositories.yunxiao"]?.toString()
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

val mavenCentralUsernameAndPassword = extra.properties["credentials.sonatype.username"]?.toString() to extra.properties["credentials.sonatype.password"]?.toString()

publishing {
  repositories {
    yunXiaoRepositoryUrls.forEach { repoUrl ->
      repoUrl.takeIf { !it.isNullOrBlank() }?.also { ru ->
        maven {
          url = uri(ru)
          yunXiaoUsernameAndPassword.takeIf { (username, password) ->
            (!username.isNullOrBlank() && !password.isNullOrBlank())
          }?.also { (u, p) ->
            credentials {
              username = u
              password = p
            }
          } ?: run {
            logger.warn("see yunxiao url: $ru not set username and password")
          }
        }
      }
    }
    mavenCentralUsernameAndPassword.takeIf { (username, password) ->
      (!username.isNullOrBlank() && !password.isNullOrBlank())
    }?.also { (u, p) ->
      maven {
        name = "MavenCentral"
        url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
        credentials {
          username = u
          password = p
        }
      }
    }
  }
  publications {
    create<MavenPublication>("mavenJava") {
      groupId = libs.versions.group.get()
      // 延迟到 afterEvaluate 中设置 artifactId 以避免配置缓存问题
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
      pub.artifactId = project.name
      pub.version = project.version.toString()
      pub.pom {
        name = "${rootProject.name}-${project.name}"
        description = project.description
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
          tag = pub.version
        }

        organization {
          name = "Yan100 Dev Group"
          url = "https://github.com/TrueNine"
        }

        issueManagement {
          system = "GitHub"
          url = "https://github.com/TrueNine/compose-server/issues"
        }

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

afterEvaluate {
  tasks.withType<PublishToMavenRepository>().configureEach {
    doFirst {
      val publication = publication as MavenPublication
      val repoUrl = repository.url.toString()
      val (username, password) = repository.credentials.username to repository.credentials.password

      val extension = when {
        publication.artifacts.any { it.extension == "pom" } -> "pom"
        publication.artifacts.any { it.extension == "toml" } -> "toml"
        publication.artifacts.any { it.extension == "jar" } -> "jar"
        publication.artifacts.isEmpty() -> "pom"
        else -> "jar"
      }

      if (artifactExists(repoUrl, username, password, publication.groupId, publication.artifactId, publication.version, extension)) {
        val msg = "skip ${publication.groupId}:${publication.artifactId}:${publication.version} to $repoUrl , because it already exists."
        logger.lifecycle(msg)
        throw StopExecutionException(msg)
      }
    }
  }
}


signing {
  val keyId = System.getenv("SIGNING_KEY_ID") ?: extra.properties["signing.keyId"]?.toString()
  val password = System.getenv("SIGNING_PASSWORD") ?: extra.properties["signing.password"]?.toString()
  val key = System.getenv("SIGNING_KEY") ?: extra.properties["signing.secretKeyRingFile"]?.toString()?.let {
    File(it).readText()
  }

  listOf(key, keyId, password)
    .takeIf { it.all { v -> !v.isNullOrBlank() } }
    ?.filterNotNull()
    ?.let { (key, keyId, password) ->
      if (!key.startsWith("-----BEGIN PGP PRIVATE KEY BLOCK-----")) {
        val msg = "key is not a valid PGP private key"
        logger.error(msg)
        error(msg)
      }
      useInMemoryPgpKeys(keyId, key, password)
    } ?: run {
    logger.warn("use gnu pgp key to command line")
    useGpgCmd()
  }
  sign(publishing.publications["mavenJava"])
}
