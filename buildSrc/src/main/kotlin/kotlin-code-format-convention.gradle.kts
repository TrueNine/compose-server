plugins {
  id("com.diffplug.spotless")
}

// https://github.com/diffplug/spotless/tree/main/plugin-gradle#quickstart
spotless {
  kotlinGradle {
    ktfmt().googleStyle().configure {
      it.setMaxWidth(80)
      it.setBlockIndent(2)
      it.setContinuationIndent(2)
      it.setRemoveUnusedImports(true)
    }
  }
  kotlin {
    licenseHeader("")
    ktfmt().googleStyle().configure {
      it.setMaxWidth(80)
      it.setBlockIndent(2)
      it.setContinuationIndent(2)
      it.setRemoveUnusedImports(true)
    }
  }
}
