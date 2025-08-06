package io.github.truenine.composeserver.holders

import jakarta.annotation.Resource
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ResourceLoader

@SpringBootTest
class ResourceHolderTest {

  lateinit var holder: ResourceHolder
    @Resource set

  lateinit var resourceLoader: ResourceLoader
    @Resource set

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
