import com.diffplug.spotless.LineEnding

plugins {
  id("com.diffplug.spotless")
}

// https://github.com/diffplug/spotless/tree/main/plugin-gradle#quickstart
spotless {
  sql {
    indentWithSpaces(2)
    lineEndings = LineEnding.UNIX
    target("**/**.sql")
    dbeaver().configFile(file(rootProject.layout.projectDirectory.file("buildSrc/.compose-config/.spotless_format_config.properties")))
  }
}

// 添加任务依赖关系
tasks.named("processResources") {
  dependsOn("spotlessSqlApply")
}
tasks.named("processTestResources") {
  dependsOn("spotlessSqlApply")
}
