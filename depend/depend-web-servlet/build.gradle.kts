version = libs.versions.compose.asProvider().get()


dependencies {
    api(libs.spring.boot.validation)
    api(libs.spring.boot.web)
    api(libs.spring.boot.tomcat)
    api(libs.spring.boot.websocket)
    implementation(project(":core"))
}
