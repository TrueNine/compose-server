plugins {
  alias(libs.plugins.org.hibernate.orm)
  alias(libs.plugins.org.jetbrains.kotlin.kapt)
  alias(libs.plugins.com.google.devtools.ksp)
  //alias(libs.plugins.org.hibernate.orm)
}

apply(plugin = libs.plugins.org.jetbrains.kotlin.kapt.get().pluginId)
apply(plugin = libs.plugins.org.jetbrains.kotlin.plugin.noarg.get().pluginId)
apply(plugin = libs.plugins.org.jetbrains.kotlin.plugin.allopen.get().pluginId)
apply(plugin = libs.plugins.com.google.devtools.ksp.get().pluginId)
apply(plugin = libs.plugins.org.hibernate.orm.get().pluginId)

version = libs.versions.compose.rds.get()

kapt {
  correctErrorTypes = true
  keepJavacAnnotationProcessors = true
  javacOptions { option("querydsl.entityAccessors", true) }
  arguments { arg("plugin", "com.querydsl.apt.jpa.JPAAnnotationProcessor") }
}

noArg { annotations("jakarta.persistence.MappedSuperclass", "jakarta.persistence.Entity") }
allOpen { annotations("jakarta.persistence.MappedSuperclass", "jakarta.persistence.Entity") }
hibernate {
  enhancement {
    enableDirtyTracking.set(true)
    enableLazyInitialization.set(true)
    enableAssociationManagement.set(true)
  }
}

sourceSets {
  main {
    resources { setSrcDirs(listOf("src/main/resources/common")) }
    java { setSrcDirs(listOf("src/main/java")) }
    kotlin { setSrcDirs(listOf("src/main/kotlin")) }
  }
}

dependencies {
  api(libs.org.springframework.boot.springBootStarterDataJpa)

  kapt(variantOf(libs.com.querydsl.querydslApt) { classifier("jakarta") })
  implementation(variantOf(libs.com.querydsl.querydslJpa) { classifier("jakarta") })

  //implementation(libs.org.springframework.springWebMvc)

  ksp(project(":ksp"))

  implementation(project(":core"))
  implementation(project(":ksp:ksp-core"))
  implementation(project(":rds:rds-core"))

  implementation(project(":security:security-crypto"))

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


