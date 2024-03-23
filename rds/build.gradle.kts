version = libs.versions.compose.get()

sourceSets

sourceSets {
  main {
    resources { setSrcDirs(listOf("src/main/resources/common")) }
    java { setSrcDirs(listOf("src/main/java")) }
    kotlin { setSrcDirs(listOf("src/main/kotlin")) }
  }
}

dependencies {
  api(libs.bundles.spring.jpa)
  api(libs.jakarta.annotationApi)

  implementation(project(":depend:depend-jvalid"))

  kapt(variantOf(libs.querydsl.apt) { classifier("jakarta") })
  implementation(variantOf(libs.querydsl.jpa) { classifier("jakarta") })
  implementation(project(":rds:rds-core"))
  implementation(libs.jakarta.annotationApi)
  implementation(project(":core"))

  implementation(libs.spring.security.crypto)
  implementation(libs.jakarta.validationApi)
  implementation(libs.spring.webmvc)
  implementation(libs.util.hutoolCore)

  testImplementation(libs.bundles.p6spySpring)
  testImplementation(libs.spring.boot.validation)
  testImplementation(libs.db.mysqlJ)
  testImplementation(project(":depend:depend-flyway"))
}

val common: SourceSet by sourceSets.creating { resources.srcDir("src/main/resources/common") }

val defaultJar by
  tasks.creating(Jar::class) {
    archiveClassifier.set("a")
    from(common.resources, sourceSets.main.get().output.classesDirs)
  }

val postgresqlJar by
  tasks.creating(Jar::class) {
    val postgresqlSourceSet: SourceSet by sourceSets.creating { resources.srcDir("src/main/resources/postgresql") }
    archiveClassifier.set("postgresql")
    from(common.resources, postgresqlSourceSet.resources, sourceSets.main.get().output.classesDirs)
  }

val mysqlJar by
  tasks.creating(Jar::class) {
    val mysqlSourceSet: SourceSet by sourceSets.creating { resources.srcDir("src/main/resources/mysql") }
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
      artifact(defaultJar) { classifier = "a" }
      artifact(postgresqlJar) { classifier = "postgresql" }
      artifact(mysqlJar) { classifier = "mysql" }
    }
  }
}

hibernate {
  enhancement {
    enableAssociationManagement.set(true)
    enableDirtyTracking.set(true)
    enableLazyInitialization.set(true)
  }
}
