plugins {
  alias(
    libs.plugins.com.google.devtools.ksp
  )
}

version = libs.versions.composeRdsCore.get()


kapt {
  correctErrorTypes = true
  keepJavacAnnotationProcessors = true
  javacOptions { option("querydsl.entityAccessors", "true") }
  arguments { arg("plugin", "com.querydsl.apt.jpa.JPAAnnotationProcessor") }
}

dependencies {
  ksp(libs.org.babyfish.jimmer.jimmerKsp)

  implementation(libs.org.springframework.boot.springBootAutoconfigure)

  implementation(project(":core"))
  implementation(project(":meta"))

  implementation(libs.org.springframework.security.springSecurityCrypto)

  implementation(libs.org.springframework.data.springDataCommons)
  implementation(libs.org.springframework.data.springDataJpa)

  implementation(libs.com.querydsl.querydslCore)


  compileOnly(variantOf(libs.com.querydsl.querydslJpa) { classifier("jakarta") })
  kapt(variantOf(libs.com.querydsl.querydslApt) { classifier("jakarta") })

  implementation(libs.org.hibernate.orm.hibernateCore)

  // jimmer
  implementation(libs.org.babyfish.jimmer.jimmerCore)
  implementation(libs.org.babyfish.jimmer.jimmerSql)
  implementation(libs.org.babyfish.jimmer.jimmerSpringBootStarter)


  testImplementation(project(":test-toolkit"))
  testImplementation(libs.org.springframework.boot.springBootStarterDataJpa)
  testRuntimeOnly(libs.bundles.p6spy)
}

publishing {
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
