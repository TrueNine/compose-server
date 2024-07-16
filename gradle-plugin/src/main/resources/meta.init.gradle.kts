val urls = "-$-"
val repos = "$-$"

val urlMap =
  urls.split(",").associate {
    val (url, mapping) = it.split("__")
    url.removeSuffix("/") to mapping
  }
val otherRepos = repos.split(",")
val backupRepoMap =
  mapOf(
    "backupRepo0" to "https://repository.sonatype.org/content/groups/forge/",
    "backupRepo1" to "https://repo.maven.apache.org/maven2",
    "backupRepo2" to "https://plugins.gradle.org/m2",
    "backupRepo3" to "https://dl.google.com/dl/android/maven2",
  )

fun RepositoryHandler.enableMirror() {
  whenObjectAdded {
    if (this is MavenArtifactRepository && !name.startsWith("backupRepo")) {
      var str = "mirror for ${javaClass.simpleName} "
      val oUrl = this.url.toString().removeSuffix("/")
      str += oUrl
      urlMap[oUrl]?.also {
        str += " -> $it"
        this.setUrl(it)
      }
    }
  }
}

fun RepositoryHandler.addNonExistsRepo(url: String, name: String? = null) {
  val rUrl = url.removeSuffix("/")
  val urlStringLists = map { (it as? MavenArtifactRepository)?.url?.toString()?.removeSuffix("/") }
  if (urlStringLists.contains(rUrl)) return
  else
    maven {
      setUrl(uri(url))
      name?.also { this.name = it }
    }
}

fun RepositoryHandler.addNonExistsMavenLocal() {
  val urlStringLists = map { (it as? MavenArtifactRepository)?.url?.toString()?.removeSuffix("/") }
  val h = urlStringLists.any { it?.startsWith("file://") ?: false }
  if (!h) mavenLocal()
}

beforeSettings {
  pluginManagement {
    repositories {
      addNonExistsMavenLocal()
      enableMirror()
      mavenCentral()
      google()
      gradlePluginPortal()
      otherRepos.forEach { addNonExistsRepo(it) }
      backupRepoMap.forEach { addNonExistsRepo(it.value, it.key) }
    }
  }
  dependencyResolutionManagement {
    repositories {
      addNonExistsMavenLocal()
      enableMirror()
      mavenCentral()
      google()
      gradlePluginPortal()
      otherRepos.forEach { addNonExistsRepo(it) }
      backupRepoMap.forEach { addNonExistsRepo(it.value, it.key) }
    }
  }
}

allprojects {
  buildscript {
    repositories {
      addNonExistsMavenLocal()
      enableMirror()
      mavenCentral()
      google()
      gradlePluginPortal()
      otherRepos.forEach { addNonExistsRepo(it) }
      backupRepoMap.forEach { addNonExistsRepo(it.value, it.key) }
    }
  }
  repositories {
    addNonExistsMavenLocal()
    enableMirror()
    mavenCentral()
    google()
    gradlePluginPortal()
    otherRepos.forEach { addNonExistsRepo(it) }
    backupRepoMap.forEach { addNonExistsRepo(it.value, it.key) }
  }
}
