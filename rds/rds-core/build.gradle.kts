version = libs.versions.compose.asProvider().get()

dependencies {
  implementation(project(":core"))
  implementation(libs.spring.data.springDataCommons)

  kapt(variantOf(libs.querydsl.apt) { classifier("jakarta") })
  implementation(variantOf(libs.querydsl.jpa) { classifier("jakarta") })
}

hibernate {
  enhancement {
    enableAssociationManagement.set(true)
    enableDirtyTracking.set(true)
    enableLazyInitialization.set(true)
  }
}
