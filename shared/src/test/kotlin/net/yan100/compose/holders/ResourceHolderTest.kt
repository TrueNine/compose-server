package net.yan100.compose.holders

import jakarta.annotation.Resource
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.test.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ResourceLoader

@SpringBootTest
class ResourceHolderTest {

  lateinit var holder: ResourceHolder
    @Resource set

  lateinit var resourceLoader: ResourceLoader
    @Resource set

  var paths: MutableList<Path> = mutableListOf()

  @BeforeTest
  fun setup() {
    val dir = System.getProperty("user.dir")

    paths.add(Paths.get(dir, "config", "data", "replace.testconfigreplacefile"))
    paths.add(Paths.get(dir, "config", "data", "replace", "replace.testconfigreplacefile"))
    paths.add(Paths.get(dir, "config", "data", "external", "external.testconfigreplacefile"))

    paths.forEach {
      if (!Files.exists(it)) {
        Files.createDirectories(it.parent)
        Files.createFile(it)
        val content = "This is a test file."
        Files.write(it, content.toByteArray())
        assertTrue("没有创建临时测试文件") { Files.exists(it) }
      }
    }
    paths.add(Paths.get(dir, "config", "data", "external"))
    paths.add(Paths.get(dir, "config", "data", "replace"))
    paths.add(Paths.get(dir, "config", "data"))
    paths.add(Paths.get(dir, "config"))
  }

  @AfterTest
  fun unmounted() {
    paths.sorted().reversed().forEach {
      if (Files.exists(it)) {
        Files.deleteIfExists(it)
      }
    }
    assertFalse("文件没有清理") { Files.exists(Paths.get(System.getProperty("user.dir"), "config")) }
  }

  @Test
  fun `test matchConfigResources`() {
    val res = holder.matchConfigResources("**/*.testconfigreplacefile").map { it.file }

    // 保证存在 4 个配置文件
    assertEquals(4, res.size)
    res
      .map { it.name }
      .let {
        assertContains(it, "replace.testconfigreplacefile")
        assertContains(it, "internal.testconfigreplacefile")
        assertContains(it, "external.testconfigreplacefile")

        assertTrue { it.count { s -> s == "replace.testconfigreplacefile" } == 2 }
      }
  }

  @Test
  fun `test get resource`() {
    val dataResource = resourceLoader.getResource("classpath:config/data/internal/internal.testconfigreplacefile")
    assertNotNull(dataResource)
    assertNotNull(dataResource.file)

    val cfgDataResource = holder.getConfigResource("internal/internal.testconfigreplacefile")
    assertNotNull(cfgDataResource)
    assertNotNull(cfgDataResource.file)
  }

  @Test
  fun `test has location`() {
    val resource = holder.getConfigResource("internal/internal.testconfigreplacefile")
    assertNotNull(resource)
    assertTrue { resource.exists() }
    assertNotNull(resource.file)
  }
}
