package io.github.truenine.composeserver.gradleplugin.generator

import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GradleGeneratorConfigTest {

  private lateinit var config: GradleGeneratorConfig

  @BeforeEach
  fun setup() {
    config = GradleGeneratorConfig()
  }

  @Test
  fun `should have correct default values`() {
    assertEquals(false, config.enabled)
    assertNotNull(config.initGradle)
    assertEquals("gradle.properties", GradleGeneratorConfig.GRADLE_PROPERTIES_NAME)
  }

  @Test
  fun `should configure init gradle through DSL`() {
    config.initGradle { initConfig ->
      initConfig.wrapperUrl("https://custom.gradle.org/distributions")
      initConfig.wrapperVersion("8.6")
      initConfig.mavenType("ali")
      initConfig.otherRepositories("https://repo1.maven.org/maven2", "https://repo2.maven.org/maven2")
    }

    val initConfig = config.initGradle
    assertEquals("https://custom.gradle.org/distributions", initConfig.wrapperUrl)
    assertEquals("8.6", initConfig.wrapperVersion)
    assertEquals(MavenRepoType.ALIYUN, initConfig.mavenType)
    assertEquals(2, initConfig.otherRepositories.size)
    assertTrue(initConfig.otherRepositories.contains("https://repo1.maven.org/maven2"))
    assertTrue(initConfig.otherRepositories.contains("https://repo2.maven.org/maven2"))
  }

  @Test
  fun `should set maven type by string`() {
    val initConfig = config.initGradle

    initConfig.mavenType("ali")
    assertEquals(MavenRepoType.ALIYUN, initConfig.mavenType)

    initConfig.mavenType("tencent")
    assertEquals(MavenRepoType.TENCENT_CLOUD, initConfig.mavenType)

    initConfig.mavenType("huawei")
    assertEquals(MavenRepoType.HUAWEI_CLOUD, initConfig.mavenType)

    initConfig.mavenType("unknown")
    assertEquals(MavenRepoType.DEFAULT, initConfig.mavenType)
  }

  @Test
  fun `should set maven type by enum`() {
    val initConfig = config.initGradle

    initConfig.mavenType(MavenRepoType.ALIYUN)
    assertEquals(MavenRepoType.ALIYUN, initConfig.mavenType)
  }

  @Test
  fun `should configure workers count`() {
    config.workers(8)
    val propertiesString = config.toPropertiesString()
    assertTrue(propertiesString.contains("org.gradle.workers.max=8"))
  }

  @Test
  fun `should throw exception for invalid workers count`() {
    assertThrows<IllegalStateException> { config.workers(2000) }

    assertThrows<IllegalStateException> { config.workers(-1) }
  }

  @Test
  fun `should configure JVM args`() {
    config.jvmArgs("-Xmx4g", "-Xms2g", "-XX:+UseG1GC")
    val propertiesString = config.toPropertiesString()
    assertTrue(propertiesString.contains("org.gradle.jvmargs=-Xmx4g -Xms2g -XX:+UseG1GC"))
  }

  @Test
  fun `should configure caching`() {
    config.caching(true)
    val propertiesString = config.toPropertiesString()
    assertTrue(propertiesString.contains("org.gradle.caching=true"))
  }

  @Test
  fun `should configure parallel`() {
    config.parallel(false)
    val propertiesString = config.toPropertiesString()
    assertTrue(propertiesString.contains("org.gradle.parallel=false"))
  }

  @Test
  fun `should configure daemon`() {
    config.daemon(false)
    val propertiesString = config.toPropertiesString()
    assertTrue(propertiesString.contains("org.gradle.daemon=false"))
  }

  @Test
  fun `should add other options`() {
    config.otherOption("custom.property", "custom.value")
    val propertiesString = config.toPropertiesString()
    assertTrue(propertiesString.contains("custom.property=custom.value"))
  }

  @Test
  fun `should generate properties string with default values`() {
    val propertiesString = config.toPropertiesString()

    assertTrue(propertiesString.contains("org.gradle.daemon=true"))
    assertTrue(propertiesString.contains("org.gradle.parallel=true"))
    assertTrue(propertiesString.contains("org.gradle.caching=false"))
    assertTrue(propertiesString.contains("org.gradle.jvmargs=-Xmx8192m -Xms4096m"))
    assertTrue(propertiesString.contains("org.gradle.workers.max="))
  }

  @Test
  fun `init gradle config should have correct defaults`() {
    val initConfig = config.initGradle

    assertEquals("https://services.gradle.org/distributions", initConfig.wrapperUrl)
    assertEquals("8.5", initConfig.wrapperVersion)
    assertEquals(MavenRepoType.DEFAULT, initConfig.mavenType)
    assertTrue(initConfig.otherRepositories.isEmpty())
    assertEquals(true, initConfig.enableSpring)
    assertEquals(true, initConfig.enableMybatisPlus)
  }
}
