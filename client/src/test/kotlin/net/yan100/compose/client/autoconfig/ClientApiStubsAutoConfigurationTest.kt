package net.yan100.compose.client.autoconfig

import jakarta.annotation.Resource
import net.yan100.compose.client.generator.TypescriptGenerator
import net.yan100.compose.meta.annotations.client.Api
import net.yan100.compose.meta.client.ClientApiStubs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

@AutoConfigureMockMvc
@SpringBootTest
class ClientApiStubsAutoConfigurationTest {
  lateinit var apis: List<ClientApiStubs> @Resource set
  lateinit var primaryClientApiStubs: ClientApiStubs @Resource set
  lateinit var handles: List<RequestMappingHandlerMapping> @Resource set
  lateinit var generator: TypescriptGenerator @Resource set


  @Test
  fun `ensure all service serial ok`() {
    assertNotNull(handles)
    assertNotEquals(0, handles.size)
    val services = primaryClientApiStubs.services
    val allServiceOperations = services.map { it.operations }.flatten()

    val allMapping = handles.map {
      it.handlerMethods
    }.reduce { acc, next -> acc + next }

    val allApiMarked = allMapping.filter { (_, method) ->
      method.hasMethodAnnotation(Api::class.java)
    }
    // 确保能找到所有标记的方法
    assertEquals(allServiceOperations.size, allApiMarked.size)
    allServiceOperations.forEach { o ->
      val findMethod = allApiMarked.values.find { it.method.name == o.name }
      assertNotNull(findMethod)
    }

    println(allMapping)
  }

  @Test
  fun `ensure primary bean register`() {
    val api = primaryClientApiStubs
    assertNotEquals(0, api.definitions.size)
    assertNotEquals(0, api.services.size)
  }

  @Test
  fun `ensure beans registered`() {
    assertNotNull(apis)
    assertNotEquals(0, apis.size)
  }
}
