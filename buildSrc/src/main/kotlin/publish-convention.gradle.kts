import org.gradle.accessors.dm.LibrariesForLibs
import java.util.*

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
      groupId = libs.versions.group.get()
      artifactId = project.name
      when {
        plugins.hasPlugin("version-catalog") -> from(components["versionCatalog"])
        plugins.hasPlugin("java-gradle-plugin") || plugins.hasPlugin("java-library") || plugins.hasPlugin("java") -> from(components["java"])

        plugins.hasPlugin("java-platform") -> from(components["javaPlatform"])

        else -> error("Unknown plugin type")
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


// 创建预检查任务
val checkPublishNeeded = tasks.register("checkPublishNeeded") {
  description = "检查是否需要执行发布任务"
  group = "publishing"

  doFirst {
    val version = project.version.toString()
    val groupId = project.group.toString()
    val artifactId = project.name

    // 创建临时配置用于检查
    val conf = project.configurations.create("publishExistenceCheck${UUID.randomUUID()}")
    try {
      project.dependencies.add(conf.name, "$groupId:$artifactId:$version")

      // 配置仓库
      publishing.repositories.withType<MavenArtifactRepository>().forEach { repo ->
        project.repositories.maven {
          url = repo.url
          credentials {
            username = yunxiaoUsername
            password = yunxiaoPassword
          }
        }
      }

      // 检查是否存在
      val exists = !conf.resolvedConfiguration.lenientConfiguration.firstLevelModuleDependencies.isEmpty()
      if (exists) {
        logger.lifecycle("版本 ${project.name}:$version 已存在于远程仓库中,跳过所有发布相关任务")
        project.extra.set("skipPublishing", true)

        // 立即禁用相关任务
        project.tasks.all { task ->
          if (task.name != "checkPublishNeeded" && (task.name.matches(
              Regex(
                ".*(publish|sign|sourcesJar|kotlinSourcesJar|javadocJar|generatePomFileFor|generateMetadataFileFor).*", RegexOption.IGNORE_CASE
              )
            ) || task.name.matches(
              Regex(
                ".*(compile|process|classes|jar|ksp|kapt).*", RegexOption.IGNORE_CASE
              )
            ) || task.name.matches(Regex(".*(spotless|test).*", RegexOption.IGNORE_CASE)))
          ) {
            task.enabled = false
            logger.info("禁用任务: ${task.path}")
            true
          } else {
            false
          }
        }
      }
    } catch (e: Exception) {
      logger.info("构件不存在或检查失败: ${e.message}")
    } finally {
      project.configurations.remove(conf)
    }
  }
}

// 在配置阶段就禁用相关任务
gradle.taskGraph.whenReady {
  if (project.extra.has("skipPublishing") && project.extra.get("skipPublishing") as Boolean) {
    allTasks.all { task ->
      if (task.name != "checkPublishNeeded" && (task.name.matches(
          Regex(
            ".*(publish|sign|sourcesJar|kotlinSourcesJar|javadocJar|generatePomFileFor|generateMetadataFileFor).*", RegexOption.IGNORE_CASE
          )
        ) || task.name.matches(Regex(".*(compile|process|classes|jar|ksp|kapt).*", RegexOption.IGNORE_CASE)) || task.name.matches(
          Regex(
            ".*(spotless|test).*", RegexOption.IGNORE_CASE
          )
        ))
      ) {
        task.enabled = false
        logger.info("禁用任务: ${task.path}")
        true
      } else {
        false
      }
    }
  }
}

// 配置任务依赖关系
afterEvaluate {
  // 找到所有需要依赖于 checkPublishNeeded 的任务
  tasks.matching { task ->
    task.name != "checkPublishNeeded" && task.name.matches(
      Regex(
        ".*(publish|sign|sourcesJar|kotlinSourcesJar|javadocJar|generatePomFileFor|generateMetadataFileFor|compile|process|classes|jar|ksp|kapt).*",
        RegexOption.IGNORE_CASE
      )
    )
  }.all { task ->
    task.onlyIf {
      !(project.extra.has("skipPublishing") && project.extra.get("skipPublishing") as Boolean)
    }
    true
  }

  // 根据项目类型配置特定任务依赖
  when {
    plugins.hasPlugin("java-platform") -> {
      tasks.named("publishToMavenLocal") {
        dependsOn(checkPublishNeeded)
        onlyIf { _ ->
          val shouldSkip = project.extra.has("skipPublishing") && project.extra.get("skipPublishing") as Boolean
          true
        }
      }
    }

    plugins.hasPlugin("version-catalog") -> {
      tasks.named("publishToMavenLocal") {
        dependsOn(checkPublishNeeded)
        onlyIf { _ ->
          project.extra.has("skipPublishing") && project.extra.get("skipPublishing") as Boolean
        }
      }
    }

    plugins.hasPlugin("java") || plugins.hasPlugin("java-library") || plugins.hasPlugin("java-gradle-plugin") -> {
      tasks.named("classes") {
        dependsOn(checkPublishNeeded)
        onlyIf { _ ->
          project.extra.has("skipPublishing") && project.extra.get("skipPublishing") as Boolean
        }
      }
    }

    else -> {
      tasks.findByName("jar")?.apply {
        dependsOn(checkPublishNeeded)
        onlyIf { _ ->
          project.extra.has("skipPublishing") && project.extra.get("skipPublishing") as Boolean
        }
      } ?: logger.warn("项目 ${project.name} 没有找到合适的任务来依赖预检查任务")
    }
  }
}
