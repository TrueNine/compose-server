package net.yan100.compose.client.autoconfig

import net.yan100.compose.core.slf4j
import net.yan100.compose.core.typing.MimeTypes
import net.yan100.compose.meta.annotations.client.Api
import net.yan100.compose.meta.client.ClientApiStubs
import net.yan100.compose.meta.client.ClientOperation
import net.yan100.compose.meta.client.ClientPostProcessApiOperationInfo
import net.yan100.compose.meta.client.ClientService
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

private val pathVariableRegex = "\\{([^}]+)}".toRegex()
private val log = slf4j<SpringClientApiStubInfoProvider>()

class SpringClientApiStubInfoProvider(
  internal var api: ClientApiStubs = ClientApiStubs(),
  internal var mappings: List<RequestMappingHandlerMapping>,
) {
  private val mergedMappingMap
    get() =
      mappings
        .map { mapping -> mapping.handlerMethods }
        .reduce { acc, cur -> acc + cur }

  private val markedApiMappingMap
    get() =
      mergedMappingMap.filter { (_, m) ->
        m.method.isAnnotationPresent(Api::class.java)
      }

  init {
    markedApiMappingMap.keys.forEach {
      if (it.methodsCondition.methods.isEmpty())
        error(
          "@Api marker HTTP PATH: ${it.pathPatternsCondition?.patterns} has no HTTP METHOD"
        )
    }
  }

  private fun findMapping(
    service: ClientService,
    operation: ClientOperation,
  ): Pair<RequestMappingInfo, HandlerMethod> {
    val f = markedApiMappingMap.map { (i, m) -> i to m }
    val infos =
      f.find { (_, m) ->
        val isServiceClass = m.beanType.name == service.typeName
        val isOperationName = m.method.name == operation.name
        val isParamsCountMatch =
          m.method.parameterCount == operation.params.size
        isServiceClass && isOperationName && isParamsCountMatch
      }
    if (infos == null)
      error(
        "can not find mapping for service: ${service.typeName}, operation: ${operation.name}"
      )
    return infos
  }

  val mappedStubs
    get() = run {
      val ser =
        api.services.map { service ->
          service.operations
            .groupBy { it.name }
            .forEach {
              if (it.value.size > 1)
                error(
                  "@Api marker HTTP endpoint function name: ${it.key} repeated, methods: [${it.value.map { it.key }}]"
                )
            }
          service.operations
            .map { operation ->
              val (rInfo, rMethod) = findMapping(service, operation)
              log.trace("handle method: {}", rMethod)
              if (rInfo.methodsCondition.methods.isEmpty())
                error(
                  "@Api marker HTTP METHOD: ${rInfo.pathPatternsCondition?.patterns} has no HTTP METHOD"
                )
              if ((rInfo.pathPatternsCondition?.patterns?.size ?: 0) > 1)
                error(
                  "@Api marker HTTP PATH: ${rInfo.pathPatternsCondition?.patterns} has more than one HTTP PATH"
                )

              val uri = rInfo.pathPatternsCondition!!.firstPattern.toString()
              val pathVariables =
                pathVariableRegex
                  .findAll(uri)
                  .map { it.groupValues[1] }
                  .toList()
              val methods = rInfo.methodsCondition.methods.map { it.name }

              val useAnyRequestBody =
                rMethod.method.parameterCount > 0 &&
                  (rMethod.method.parameters?.any {
                    it.isAnnotationPresent(RequestBody::class.java)
                  } == true)
              val useAnyRequestPart =
                rMethod.method.parameterCount > 0 &&
                  (rMethod.method.parameters?.any {
                    it.isAnnotationPresent(RequestPart::class.java)
                  } == true)
              if (useAnyRequestBody && methods.any { it == "GET" }) {
                log.warn(
                  "@Api marker HTTP PATH: {} has use @RequestBody in GET method",
                  rInfo.pathPatternsCondition?.patterns,
                )
              }

              val acceptType =
                when {
                  useAnyRequestPart && useAnyRequestBody ->
                    error(
                      "@Api marker HTTP PATH: $uri has use @RequestBody and @RequestPart in same method"
                    )
                  useAnyRequestBody -> MimeTypes.JSON.value
                  useAnyRequestPart -> MimeTypes.MULTIPART_FORM_DATA.value
                  else -> MimeTypes.URL.value
                }

              val requestInfo =
                ClientPostProcessApiOperationInfo(
                  mappedUris = listOf(uri),
                  supportedMethods = methods,
                  pathVariables = pathVariables,
                  requestAcceptType = acceptType,
                )
              operation.copy(requestInfo = requestInfo)
            }
            .let { service.copy(operations = it) }
        }
      api.copy(services = ser)
    }
}
