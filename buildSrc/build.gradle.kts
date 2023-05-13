plugins {
  `kotlin-dsl`
}

val repoAli = "https://maven.aliyun.com/"
repositories {
  maven(url = uri("${repoAli}repository/public"))
  maven(url = uri("${repoAli}repository/gradle-plugin"))
  maven(url = uri("${repoAli}repository/jcenter"))
  maven(url = uri("${repoAli}repository/central"))
  maven(url = uri("${repoAli}repository/spring"))
  maven(url = uri("https://repo.huaweicloud.com/repository/maven/"))
  maven(url = uri("https://repo.huaweicloud.com/repository/kunpeng/maven/"))
  mavenLocal()
  mavenCentral()
  gradlePluginPortal()
  google()
}
