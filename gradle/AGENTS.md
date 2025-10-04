## libs.versions.toml File Description

- `project` Current project dependency version number
- `java` Current project Java version number
- `org-gradle` Gradle version number
- `intellij-platform-*` IntelliJ plugin development version number

### Usage in `buildSrc` or `includeBuild`

Introduce `libs.versions.toml` as a dependency in the `settings.gradle.kts` of `buildSrc` or `build-logic` to achieve unified project-wide version management

```kotlin
versionCatalogs {
  create("libs") {
    from(files("../gradle/libs.versions.toml"))
  }
}
```

Then inject in `build.gradle.kts`

```kotlin
dependencies {
  implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
```

If you need to use it in plugins, define variables like this:

```kotlin
val libs = the<org.gradle.accessors.dm.LibrariesForLibs>()
```