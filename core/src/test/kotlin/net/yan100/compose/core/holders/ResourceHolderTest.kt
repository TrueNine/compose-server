package net.yan100.compose.core.holders

import jakarta.annotation.Resource
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ResourceLoader
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest
class ResourceHolderTest {

  lateinit var holder: ResourceHolder @Resource set
  lateinit var resourceLoader: ResourceLoader @Resource set

  @Test
  fun `test get resource`() {
    val dataResource = resourceLoader.getResource("classpath:data/a.txt")
    assertNotNull(dataResource)
    assertNotNull(dataResource.file)

    val cfgDataResource = holder.getConfigResource("config/data/a.txt")
    assertNotNull(cfgDataResource)
    assertNotNull(cfgDataResource.file)
  }

  @Test
  fun `test has location`() {
    val resource = holder.getConfigResource("a.txt")
    assertNotNull(resource)
    assertTrue { resource.exists() }
    assertNotNull(resource.file)
  }
}
