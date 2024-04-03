version = libs.versions.compose.get()

dependencies {
  implementation(project(":core"))
  implementation(project(":depend:depend-jvalid"))
  implementation(libs.spring.data.springDataCommons)

  testImplementation(project(":depend:depend-web-servlet"))
  testImplementation(libs.org.springframework.boot.spring.boot.starter.json)

  kapt(variantOf(libs.com.querydsl.querydsl.apt) { classifier("jakarta") })
  implementation(variantOf(libs.com.querydsl.querydsl.jpa) { classifier("jakarta") })
}

hibernate {
  enhancement {
    enableAssociationManagement.set(true)
    enableDirtyTracking.set(true)
    enableLazyInitialization.set(true)
  }
}
