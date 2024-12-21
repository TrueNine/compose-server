package net.yan100.compose.client.generator

import jakarta.annotation.Resource
import net.yan100.compose.testtookit.assertNotEmpty
import net.yan100.compose.testtookit.log
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest
class LazyGeneratorTest {
  lateinit var gen: LazyGenerator @Resource set

  @Test
  fun `ensure every operation has requestInfo`() {
    val stub = gen.mappedStubs
    assertNotEquals(0, stub.services.size)
    stub.services.forEach { service ->
      service.operations.forEach { operation ->
        val reqInfo = operation.requestInfo
        assertNotNull(reqInfo)
        assertNotEmpty { reqInfo.mappedUris }
        reqInfo.mappedUris.forEach { assertTrue { it.isNotBlank() } }
        assertNotEquals(0, reqInfo.supportedMethods.size)
        log.info("requestInfo: {}", operation.requestInfo)
      }
    }
  }
}
