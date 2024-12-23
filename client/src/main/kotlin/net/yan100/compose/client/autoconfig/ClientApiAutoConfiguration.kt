package net.yan100.compose.client.autoconfig

import com.fasterxml.jackson.databind.ObjectMapper
import net.yan100.compose.client.generator.TypescriptFileGenerator
import net.yan100.compose.core.slf4j
import net.yan100.compose.meta.client.ClientApi
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.support.PathMatchingResourcePatternResolver

private val log = slf4j<ClientApiAutoConfiguration>()

@Configuration
class ClientApiAutoConfiguration : ApplicationRunner {
  private val resolver: PathMatchingResourcePatternResolver = PathMatchingResourcePatternResolver()
  lateinit var api: ClientApi

  @Bean
  fun listsClientApis(
    mapper: ObjectMapper
  ): ClientApi {
    val resources = resolver.getResources("classpath:META-INF/compose-client/*-client-ts.stub.json")
      .filter { it.exists() }
    val apis = resources.map {
      mapper.readValue(it.inputStream, ClientApi::class.java)
    }

    val defs = apis.map { it.definitions }.flatten().distinct().toMutableList()
    val services = apis.map { it.services }.flatten().distinct().toMutableList()
    api = ClientApi(
      definitions = defs,
      services = services
    )
    return api
  }

  @Bean
  fun tsApiLazyGenerator(
    ctx: ApplicationContext,
    api: ClientApi
  ): TypescriptFileGenerator {
    return TypescriptFileGenerator(ctx, api)
  }

  override fun run(args: ApplicationArguments?) {
    api.let {
      if (it.definitions.isEmpty()) {
        log.warn("api stub definitions is empty")
      }
      if (it.services.isEmpty()) {
        log.warn("api stub services is empty")
      }
    }
  }
}
