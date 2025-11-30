package io.github.truenine.composeserver.gradleplugin

import io.github.truenine.composeserver.gradleplugin.consts.ConstantTest
import io.github.truenine.composeserver.gradleplugin.consts.GradleProjectDelegatorTest
import io.github.truenine.composeserver.gradleplugin.dotenv.*
import io.github.truenine.composeserver.gradleplugin.entrance.ConfigEntranceTest
import io.github.truenine.composeserver.gradleplugin.generator.GradleGeneratorConfigTest
import io.github.truenine.composeserver.gradleplugin.generator.MavenRepoTypeTest
import io.github.truenine.composeserver.gradleplugin.jar.JarExtensionConfigTest
import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite

/**
 * Test suite for the Gradle plugin
 *
 * This suite runs all unit tests for the compose-server gradle plugin. It covers configuration classes, utility functions, constants, and the main plugin
 * class.
 */
@Suite
@SelectClasses(
  MainTest::class,
  ConfigEntranceTest::class,
  GradleGeneratorConfigTest::class,
  JarExtensionConfigTest::class,
  MavenRepoTypeTest::class,
  DotenvConfigTest::class,
  DotenvLoaderTest::class,
  DotenvIntegrationTest::class,
  ConstantTest::class,
  GradleProjectDelegatorTest::class,
  GradleFnsTest::class,
  FunctionsTest::class,
)
class GradlePluginTestSuite
