package itest.integrate.depend.itest.integrate.oss.minio

import io.github.truenine.composeserver.oss.IObjectStorageService
import io.github.truenine.composeserver.testtoolkit.log
import io.github.truenine.composeserver.testtoolkit.testcontainers.IOssMinioContainer
import jakarta.annotation.Resource
import kotlin.test.BeforeTest
import kotlin.test.Test
import org.junit.jupiter.api.assertNotNull
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class AutoConfigurationPropertiesTest : IOssMinioContainer {
  @Resource lateinit var oss: IObjectStorageService

  @BeforeTest fun setup(): Unit = minio { assertNotNull(oss) }

  @Test
  fun a() {
    log.info("123")
  }
}
