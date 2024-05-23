version = libs.versions.compose.rds.get()

plugins { alias(libs.plugins.ktKsp) }

sourceSets {
  main {
    resources { setSrcDirs(listOf("src/main/resources/common")) }
    java { setSrcDirs(listOf("src/main/java")) }
    kotlin { setSrcDirs(listOf("src/main/kotlin")) }
  }
}

dependencies {
  api(libs.bundles.spring.jpa)
  api(libs.jakarta.annotation.jakarta.annotation.api)

  implementation(project(":depend:depend-jvalid"))

  ksp(project(":ksp"))
  implementation(project(":ksp"))
  kapt(variantOf(libs.com.querydsl.querydsl.apt) { classifier("jakarta") })

  implementation(variantOf(libs.com.querydsl.querydsl.jpa) { classifier("jakarta") })
  implementation(project(":rds:rds-core"))
  implementation(libs.jakarta.annotation.jakarta.annotation.api)
  implementation(project(":core"))

  implementation(libs.spring.security.crypto)
  implementation(libs.jakarta.validationApi)
  implementation(libs.spring.webmvc)
  implementation(libs.cn.hutool.hutool.core)

  testImplementation(libs.bundles.p6spySpring)
  testImplementation(libs.spring.boot.validation)
  testImplementation(libs.com.mysql.mysql.connector.j)
}

val common: SourceSet by sourceSets.creating { resources.srcDir("src/main/resources/common") }

val defaultJar by
  tasks.creating(Jar::class) {
    archiveClassifier.set("a")
    from(common.resources, sourceSets.main.get().output.classesDirs)
  }

val postgresqlJar by
  tasks.creating(Jar::class) {
    val postgresqlSourceSet: SourceSet by
      sourceSets.creating {
        resources.srcDir("src/main/resources/postgresql")
        dependencies {
          implementation(libs.org.flywaydb.flyway.core)
          runtimeOnly(libs.org.flywaydb.flyway.mysql)
        }
      }
    archiveClassifier.set("postgresql")
    from(common.resources, postgresqlSourceSet.resources, sourceSets.main.get().output.classesDirs)
  }

val mysqlJar by
  tasks.creating(Jar::class) {
    val mysqlSourceSet: SourceSet by
      sourceSets.creating {
        resources.srcDir("src/main/resources/mysql")
        dependencies {
          implementation(libs.org.flywaydb.flyway.core)
          runtimeOnly(libs.org.flywaydb.flyway.database.postgresql)
        }
      }
    archiveClassifier.set("mysql")
    from(mysqlSourceSet.resources, sourceSets.main.get().output.classesDirs)
  }

// artifacts {
//    add("archives", commonJar)
//    add("archives", postgresqlJar)
//    add("archives", mysqlJar)
// }

publishing {
  publications {
    create<MavenPublication>("rdsMaven") {
      groupId = project.group.toString()
      artifactId = project.name
      version = project.version.toString()
      from(components["java"])
      // artifact(defaultJar) { classifier = "" }
      artifact(postgresqlJar) { classifier = "postgresql" }
      artifact(mysqlJar) { classifier = "mysql" }
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

signing {
  useGpgCmd()
  sign(publishing.publications["maven"])
}

hibernate {
  enhancement {
    enableAssociationManagement.set(true)
    enableDirtyTracking.set(true)
    enableLazyInitialization.set(true)
  }
}
