plugins {
  id("com.diffplug.spotless")
  id("buildlogic.repositories-conventions")
}

spotless {
  kotlinGradle {
    ktfmt().googleStyle().configure {
      it.setMaxWidth(160)
      it.setBlockIndent(2)
      it.setContinuationIndent(2)
      it.setRemoveUnusedImports(true)
    }
  }
  // 只在项目应用了 Kotlin 插件时才配置 Kotlin 规则
  if (plugins.hasPlugin("org.jetbrains.kotlin.jvm") ||
    plugins.hasPlugin("org.jetbrains.kotlin.android") ||
    plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")
  ) {
    kotlin {
      target("**/*.kt")
      targetExclude("**/build/generated/**")
      licenseHeader("")
      ktfmt().googleStyle().configure {
        it.setMaxWidth(160)
        it.setBlockIndent(2)
        it.setContinuationIndent(2)
        it.setRemoveUnusedImports(true)
      }
    }
  }
}
