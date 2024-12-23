package net.yan100.compose.client.generator

import net.yan100.compose.client.domain.TypescriptEnum
import net.yan100.compose.client.domain.TypescriptFile
import net.yan100.compose.client.templates.UtilsTemplate
import net.yan100.compose.client.toTypescriptEnum
import net.yan100.compose.meta.annotations.client.Api
import net.yan100.compose.meta.client.*
import net.yan100.compose.meta.types.TypeKind
import org.springframework.context.ApplicationContext
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping

private val pathVariableRegex = "\\{([^}]+)}".toRegex()

class TypescriptFileGenerator(
  private val ctx: ApplicationContext, private val api: ClientApi
) {

  internal fun renderEnum(tsEnum: TypescriptEnum): TypescriptFile {
    return TypescriptFile.Enum(tsEnum)
  }

  internal fun renderEnumsToFiles(enumDefinitions: List<ClientType> = emptyList()): List<TypescriptFile> {
    return enumDefinitions.filter { it.typeKind == TypeKind.ENUM_CLASS }.map { enumClientType ->
      val constants = enumClientType.resolveEnumConstants()
      renderEnum(
        enumClientType.toTypescriptEnum().copy(
          constants = constants
        )
      )
    }
  }

  fun renderExecutor(): TypescriptFile {
    return TypescriptFile.SingleUtils(
      name = "Executor",
      code = UtilsTemplate.renderExecutor(),
      usedNames = listOf("HTTPMethod", "BodyType")
    )
  }

  private val mergedMappingMap
    get() = ctx.getBeansOfType(RequestMappingHandlerMapping::class.java).values.map { mapping ->
      mapping.handlerMethods
    }.reduce { acc, cur -> acc + cur }

  /**
   * 所有被标记的 HTTP Mapping
   */
  val markedApiMappingMap
    get() = mergedMappingMap.filter { (_, m) ->
      m.method.isAnnotationPresent(Api::class.java)
    }


  init {
    // validate all method
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


  /**
   * 已经映射完毕的 ClientType
   */
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
