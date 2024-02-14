pluginManagement {
    repositories {
        mavenLocal()
        maven(url = uri("https://repo.huaweicloud.com/repository/maven/"))
        maven(url = uri("https://repo.spring.io/milestone"))
        gradlePluginPortal()
        mavenCentral()
    }
}
plugins {
    // https://plugins.gradle.org/plugin/org.gradle.toolchains.foojay-resolver-convention
    id("org.gradle.toolchains.foojay-resolver-convention") version ("0.7.0")
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
}
