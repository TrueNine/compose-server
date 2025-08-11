## libs.versions.toml 文件说明

- `project` 当前项目依赖版本号
- `java` 当前项目 使用的版本号
- `gradle` gradle 使用的版本号
- `intellij-platform-*` intellij 插件开发版本号

### `buildSrc` 以及 `includeBuild` 中使用

在 `buildSrc` or `build-logic` 的 `settiongs.gradle.kts` 中引入 `libs.versions.toml` 作为依赖，以达到全项目版本统一

```kotlin
versionCatalogs {
  create("libs") {
    from(files("../gradle/libs.versions.toml"))
  }
}
```

然后在 `build.gradle.kts` 中进行注入

```kotlin
dependencies {
  implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
```

如果需要在插件中使用时，需要像下面这样定义变量

```kotlin
val libs = the<org.gradle.accessors.dm.LibrariesForLibs>()
```
