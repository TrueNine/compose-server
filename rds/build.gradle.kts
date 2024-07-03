version = libs.versions.compose.rds.asProvider().get()

plugins { alias(libs.plugins.com.google.devtools.ksp) }

sourceSets {
  main {
    resources { setSrcDirs(listOf("src/main/resources/common")) }
    java { setSrcDirs(listOf("src/main/java")) }
    kotlin { setSrcDirs(listOf("src/main/kotlin")) }
  }
}

dependencies {
  api(libs.bundles.jpa)
  kapt(variantOf(libs.com.querydsl.querydslApt) { classifier("jakarta") })
  implementation(variantOf(libs.com.querydsl.querydslJpa) { classifier("jakarta") })
  implementation(libs.org.springframework.security.springSecurityCrypto)
  implementation(libs.org.springframework.springWebMvc)
  implementation(libs.cn.hutool.hutoolCore)

  testImplementation(libs.org.springframework.boot.springBootStarterValidation)

  ksp(project(":ksp"))
  implementation(project(":ksp:ksp-core"))
  implementation(project(":depend:depend-jvalid"))
  implementation(project(":rds:rds-core"))
  implementation(project(":core"))

  testImplementation(project(":test-toolkit"))
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
          implementation(libs.org.flywaydb.flywayCore)
          runtimeOnly(libs.org.flywaydb.flywayMysql)
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
          implementation(libs.org.flywaydb.flywayCore)
          runtimeOnly(libs.org.flywaydb.flywayDatabasePostgresql)
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
