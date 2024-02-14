version = libs.versions.compose.asProvider().get()

dependencies {
    api(libs.bundles.knife4j)
    implementation(project(":core"))

    api(libs.spring.doc.webmvcUi3)
    api(libs.jakarta.openapiV3Annotations)
}
