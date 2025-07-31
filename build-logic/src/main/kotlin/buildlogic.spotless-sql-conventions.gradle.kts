import com.diffplug.spotless.LineEnding
import gradle.kotlin.dsl.accessors._440fea8aa94a501f8f7bd4c32ad12a39.spotless

plugins {
  id("com.diffplug.spotless")
  id("buildlogic.repositories-conventions")
}

spotless {
  sql {
    lineEndings = LineEnding.UNIX
    target("**/**.sql")
    idea()
      .codeStyleSettingsPath(
        rootProject
          .layout
          .projectDirectory
          .file(".idea/codeStyles/Project.xml")
          .asFile
          .absolutePath
      )
  }
}
