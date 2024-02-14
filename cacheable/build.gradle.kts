version = libs.versions.compose.asProvider().get()

dependencies {
    api(libs.bundles.spring.redis)
    api(libs.cache.caffeine)
    implementation(project(":core"))
}
