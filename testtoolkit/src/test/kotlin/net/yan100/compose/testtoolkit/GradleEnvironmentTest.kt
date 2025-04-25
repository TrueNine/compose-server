package net.yan100.compose.testtoolkit

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/** 确保能够启动 */
class GradleEnvironmentTest {
  private val logger = log

  @BeforeTest
  fun setup() {
    assertNotNull(logger)
  }

  @Test
  fun `gradle 读取到了 project env 文件`() {
    val e = System.getenv("TEST_A")
    assertNotNull(e)
    assertEquals(e, "1")
  }

  @Test
  fun `确保 能够 加载到 gradle 环境变量`() {
    val e = System.getenv("ENV_FILE_EMPTY")
    assertNull(e)
  }

  @Test
  fun `确保 测试类 能够 正常启动`() {
    log.info("launched")
  }
}
