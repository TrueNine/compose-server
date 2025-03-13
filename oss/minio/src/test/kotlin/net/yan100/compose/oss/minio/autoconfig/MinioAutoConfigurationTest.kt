package net.yan100.compose.oss.minio.autoconfig

import io.minio.MinioClient
import jakarta.annotation.Resource
import net.yan100.compose.oss.minio.MinioClientWrapper
import org.springframework.beans.factory.getBean
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import kotlin.test.Test
import kotlin.test.assertNotNull

@SpringBootTest
class MinioAutoConfigurationTest {
  lateinit var ctx: ApplicationContext @Resource set

  @Test
  fun `确保 初始化了 client wrapper`() {
    val wrapper = ctx.getBean<MinioClientWrapper>()
    assertNotNull(wrapper)
    assertNotNull(wrapper.exposedBaseUrl)
  }

  @Test
  fun `确保 只有 bean 的情况下，初始化了 minio 客户端`() {
    val client = ctx.getBean<MinioClient>()
    assertNotNull(client)
  }
}
