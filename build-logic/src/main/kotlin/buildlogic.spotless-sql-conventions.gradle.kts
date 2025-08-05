import com.diffplug.spotless.LineEnding

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
