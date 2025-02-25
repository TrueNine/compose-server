package net.yan100.compose.client.autoconfig

import com.fasterxml.jackson.databind.ObjectMapper
import net.yan100.compose.client.generator.TypescriptGenerator
import net.yan100.compose.core.slf4j
import net.yan100.compose.meta.client.ClientApiStubs
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.support.PathMatchingResourcePatternResolver

private val log = slf4j<ClientApiAutoConfiguration>()

@Configuration
class ClientApiAutoConfiguration : ApplicationRunner {
  private val resolver: PathMatchingResourcePatternResolver =
    PathMatchingResourcePatternResolver()
  lateinit var api: ClientApiStubs

  @Bean
  @ConditionalOnMissingBean
  fun listsClientApiStubs(
    mapper: ObjectMapper,
    provider: SpringClientApiStubInfoProvider,
  ): ClientApiStubs {
    val resources =
      resolver
        .getResources("classpath:META-INF/compose-client/*-client-ts.stub.json")
        .filter { it.exists() }
    val apis =
      resources.map {
        mapper.readValue(it.inputStream, ClientApiStubs::class.java)
      }
    val definitions = apis.map { it.definitions }.flatten().distinct()
    val services = apis.map { it.services }.flatten().distinct()
    api = ClientApiStubs(services = services, definitions = definitions)
    provider.api = api
    return provider.mappedStubs
  }

  @Bean
  @ConditionalOnMissingBean
  fun springClientApiStubInfoProvider(
    ctx: ApplicationContext
  ): SpringClientApiStubInfoProvider =
    SpringClientApiStubInfoProvider(
      mappings =
        ctx
          .getBeansOfType(
            org.springframework.web.servlet.mvc.method.annotation
                .RequestMappingHandlerMapping::class
              .java
          )
          .values
          .toList()
    )

  @Bean
  @ConditionalOnMissingBean
  fun tsApiLazyGenerator(apiStub: ClientApiStubs): TypescriptGenerator {
    return TypescriptGenerator { apiStub }
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
