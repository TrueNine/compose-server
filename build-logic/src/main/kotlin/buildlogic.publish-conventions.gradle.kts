val libs = the<org.gradle.accessors.dm.LibrariesForLibs>()

plugins {
  id("com.vanniktech.maven.publish")
}

mavenPublishing {
  publishToMavenCentral(automaticRelease = true)
  signAllPublications()
}

mavenPublishing {
  coordinates(
    groupId = libs.versions.group.get(),
    artifactId = project.name,
    version = libs.versions.project.get()
  )
  pom {
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
