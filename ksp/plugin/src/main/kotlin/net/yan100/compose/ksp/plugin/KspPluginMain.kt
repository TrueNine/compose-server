package net.yan100.compose.ksp.plugin

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class KspPluginMain : SymbolProcessorProvider {
  override fun create(
    environment: SymbolProcessorEnvironment
  ): SymbolProcessor {
    environment.logger.info(
      "start ksp generate, kspVersion: ${environment.kspVersion}, kotlinVersion${environment.kspVersion}, platforms: ${environment.platforms}, options: ${environment.options}"
    )
    return KspPluginProcessor(environment)
  }
}
