import com.diffplug.spotless.LineEnding

plugins {
  id("com.diffplug.spotless")
  id("buildlogic.repositories-conventions")
}

spotless {
  sql {
    lineEndings = LineEnding.UNIX
    target("**/**.sql")
  }
}
