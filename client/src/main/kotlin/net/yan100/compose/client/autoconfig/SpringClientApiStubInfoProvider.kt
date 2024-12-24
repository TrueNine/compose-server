package net.yan100.compose.client.autoconfig

import net.yan100.compose.meta.annotations.client.Api
import net.yan100.compose.meta.client.ClientApiStubs
import net.yan100.compose.meta.client.ClientOperation
import net.yan100.compose.meta.client.ClientPostProcessApiOperationInfo
import net.yan100.compose.meta.client.ClientService
import org.springframework.context.ApplicationContext
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

private val pathVariableRegex = "\\{([^}]+)}".toRegex()

class SpringClientApiStubInfoProvider(
  private val ctxProvider: () -> ApplicationContext,
) {
  private val ctx get() = ctxProvider()
  private val api get() = ctx.getBean(ClientApiStubs::class.java)

  private val mergedMappingMap
    get() = ctx.getBeansOfType(RequestMappingHandlerMapping::class.java).values.map { mapping ->
      mapping.handlerMethods
    }.reduce { acc, cur -> acc + cur }
  private val markedApiMappingMap
    get() = mergedMappingMap.filter { (_, m) ->
      m.method.isAnnotationPresent(Api::class.java)
    }

  init {
    markedApiMappingMap.keys.forEach {
      if (it.methodsCondition.methods.size == 0) error("@Api marker HTTP PATH: ${it.pathPatternsCondition?.patterns} has no HTTP METHOD")
    }
  }

  private fun findMapping(service: ClientService, operation: ClientOperation): Pair<RequestMappingInfo, HandlerMethod> {
    val f = markedApiMappingMap.map { (i, m) -> i to m }
    val infos = f.find { (_, m) ->
      val isServiceClass = m.beanType.name == service.typeName
      val isOperationName = m.method.name == operation.name
      val isParamsCountMatch = m.method.parameterCount == operation.parameterTypes.size
      isServiceClass && isOperationName && isParamsCountMatch
    }
    if (infos == null) error("can not find mapping for service: ${service.typeName}, operation: ${operation.name}")
    return infos
  }

  val mappedStubs
    get() = run {
      val ser = api.services.map { service ->
        service.operations.map { operation ->
          val (rInfo) = findMapping(service, operation)
          if (rInfo.methodsCondition.methods.size <= 0) error("@Api marker HTTP METHOD: ${rInfo.pathPatternsCondition?.patterns} has no HTTP METHOD")
          if ((rInfo.pathPatternsCondition?.patterns?.size
              ?: 0) > 1
          ) error("@Api marker HTTP PATH: ${rInfo.pathPatternsCondition?.patterns} has more than one HTTP PATH")

          val uri = rInfo.pathPatternsCondition!!.firstPattern.toString()
          val pathVariables = pathVariableRegex.findAll(uri).map { it.groupValues[1] }.toList()
          val methods = rInfo.methodsCondition.methods.map { it.name }.toList()

          val requestInfo = ClientPostProcessApiOperationInfo(
            mappedUris = listOf(uri), supportedMethods = methods, pathVariables = pathVariables, requestAcceptType = "", responseContentType = ""
          )
          operation.copy(requestInfo = requestInfo)
        }.let { service.copy(operations = it) }
      }
      api.copy(services = ser)
    }
}
