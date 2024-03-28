fun RepositoryHandler.enableMirror() {
  all {
    if (this is MavenArtifactRepository) {
      logger.lifecycle("peek  [$url]")
    }
  }
}

logger.lifecycle("peek  project  ${project.name}")

gradle.allprojects {
  logger.lifecycle("peek  allproject")
  buildscript {
    logger.lifecycle("peek  buildscript")
    repositories.enableMirror()
  }
  logger.lifecycle("peek  repositories")
  repositories.enableMirror()
}

gradle.beforeSettings {
  logger.lifecycle("peek  settings")
  logger.lifecycle("peek  settings  pluginManagement")
  pluginManagement.repositories.enableMirror()
  logger.lifecycle("peek  settings  dependencyResolutionManagement")
  dependencyResolutionManagement.repositories.enableMirror()
}
