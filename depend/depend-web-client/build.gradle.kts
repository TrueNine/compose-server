version = libs.versions.compose.asProvider().get()

dependencies {
    api(libs.spring.boot.webflux)
    implementation(project(":core"))
}
