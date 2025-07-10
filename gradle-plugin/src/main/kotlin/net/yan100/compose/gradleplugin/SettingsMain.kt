package net.yan100.compose.gradleplugin

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

class SettingsMain : Plugin<Settings> {
  override fun apply(target: Settings) {
    val properties = target.extensions.extraProperties.properties

    target.dependencyResolutionManagement { drm -> drm.versionCatalogs { vc -> vc.create("libs") {} } }
    target.pluginManagement {
      it.repositories { ir ->
        ir.chinaRegionRepositories()
        ir.forEach { e -> println(e) }
      }
    }
  }

  companion object
}
