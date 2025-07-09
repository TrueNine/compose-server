import com.diffplug.spotless.LineEnding

plugins {
  id("com.diffplug.spotless")
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
  kotlin {
    licenseHeader("")
    ktfmt().googleStyle().configure {
      it.setMaxWidth(160)
      it.setBlockIndent(2)
      it.setContinuationIndent(2)
      it.setRemoveUnusedImports(true)
    }
  }
  sql {
    lineEndings = LineEnding.UNIX
    target("**/**.sql")
    dbeaver().configFile(file(rootProject.layout.projectDirectory.file("build-logic/.compose-config/.spotless_format_config.properties")))
  }
}
