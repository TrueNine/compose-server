import net.yan100.compose.plugin.Repos

version = libs.versions.compose.asProvider().get()

dependencies {
  api(libs.bundles.spring.jpa)
  api(libs.jakarta.annotationApi)

  kapt(variantOf(libs.querydsl.apt) { classifier("jakarta") })
  api(variantOf(libs.querydsl.jpa) { classifier("jakarta") })
  api(project(":rds:rds-core"))
  api(libs.jakarta.annotationApi)

  implementation(project(":core"))

  testImplementation(libs.bundles.p6spySpring)

  implementation(libs.spring.security.crypto)
  implementation(libs.jakarta.validationApi)
  implementation(libs.spring.webmvc)
  implementation(libs.util.hutool.core)
  testImplementation(libs.spring.boot.validation)
  testImplementation(libs.db.mysqlJ)

  testImplementation(project(":depend:depend-flyway"))
}

val postgresqlSourceSet: SourceSet by
  sourceSets.creating { resources.srcDir("src/main/resources/postgresql") }

val postgresqlJar by
  tasks.creating(Jar::class) {
    archiveClassifier.set("postgresql")
    from(postgresqlSourceSet.output, sourceSets["main"].output)
  }

val mysqlSourceSet: SourceSet by
  sourceSets.creating { resources.srcDir("src/main/resources/mysql") }
val mysqlJar by
  tasks.creating(Jar::class) {
    archiveClassifier.set("mysql")
    from(mysqlSourceSet.output, sourceSets["main"].output)
  }

artifacts {
  add("archives", postgresqlJar)
  add("archives", mysqlJar)
}

publishing {
  repositories {
    maven(
      url =
        uri(
          if (version.toString().uppercase().contains("SNAPSHOT")) Repos.yunXiaoSnapshot
          else Repos.yunXiaoRelese
        )
    ) {
      credentials {
        username = Repos.Credentials.yunXiaoUsername
        password = Repos.Credentials.yunXiaoPassword
      }
    }
  }

  publications {
    create<MavenPublication>("rdsMaven") {
      groupId = project.group.toString()
      artifactId = project.name
      version = project.version.toString()
      from(components["java"])
      artifact(postgresqlJar) { classifier = "postgresql" }
      artifact(mysqlJar) { classifier = "mysql" }
    }
  }
}

hibernate { enhancement { enableAssociationManagement.set(true) } }
