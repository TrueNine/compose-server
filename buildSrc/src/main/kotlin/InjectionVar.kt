import org.gradle.api.Plugin
import org.gradle.api.invocation.Gradle
import org.gradle.kotlin.dsl.extra

// TODO 无用
class InjectionVar : Plugin<Gradle> {
  override fun apply(target: Gradle) {
    target.rootProject.extra["V.Lang.kotlin"] = "1.8.20"
  }
}