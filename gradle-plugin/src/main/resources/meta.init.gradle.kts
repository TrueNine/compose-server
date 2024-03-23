val urls = "$$$$$$$$"

val urlMappings =
  urls
    .split(",")
    .associate {
      val (url, mapping) = it.split("________")
      url to mapping
    }

fun RepositoryHandler.enableMirror() {
  all {
    if (this is MavenArtifactRepository) {
      val oUrl = this.url.toString().removeSuffix("/")
      urlMappings[oUrl]?.also {
        logger.lifecycle("Repository[$url] is mirrored to $it")
        this.setUrl(it)
      }
    }
  }
}

gradle.allprojects {
  buildscript { repositories.enableMirror() }
  repositories.enableMirror()
}

gradle.beforeSettings {
  pluginManagement.repositories.enableMirror()
  dependencyResolutionManagement.repositories.enableMirror()
}
