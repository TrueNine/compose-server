plugins {
  `kotlin-dsl`
}

val repoAli = "https://maven.aliyun.com/"
repositories {
  maven("${repoAli}repository/central")
  maven("${repoAli}repository/jcenter")
  maven("${repoAli}repository/public")
  maven("${repoAli}repository/gradle-plugin")
  maven("${repoAli}repository/spring")
  mavenCentral()
  gradlePluginPortal()
  google()
}
