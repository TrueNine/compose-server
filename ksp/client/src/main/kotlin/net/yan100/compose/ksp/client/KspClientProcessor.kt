package net.yan100.compose.ksp.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import net.yan100.compose.ksp.toolkit.*
import net.yan100.compose.ksp.toolkit.kotlinpoet.Libs
import net.yan100.compose.meta.annotations.client.Api
import net.yan100.compose.meta.client.ClientApiStubs
import net.yan100.compose.meta.client.ClientOperation
import net.yan100.compose.meta.client.ClientProp
import net.yan100.compose.meta.client.ClientService
import java.io.OutputStreamWriter
import java.util.*

class KspClientProcessor(
  private val environment: SymbolProcessorEnvironment, private val mapper: ObjectMapper
) : SymbolProcessor {
  private var generated = false

  @OptIn(KspExperimental::class)
  override fun process(resolver: Resolver): List<KSAnnotated> {
    val apiAnnotationName = Libs.net.yan100.compose.meta.annotations.client.Api.qualifiedName
    val nextHandles = mutableListOf<KSAnnotated>()
    val apiClasses = resolver.getSymbolsWithAnnotation(apiAnnotationName).filterIsInstance<KSClassDeclaration>().toList().filter {
      if (it.validate()) {
        true
      } else {
        nextHandles.add(it)
        false
      }
    }
    if (nextHandles.isNotEmpty()) {
      val all = (nextHandles + apiClasses)
      return all
    }

    val allApis = apiClasses.map { cls ->
      val funList = cls.getAllFunctions().filter {
        it.isAnnotationPresent(Api::class)
      }.toList()
      cls to funList
    }.toList()

    val allParameterTypes = allApis.map { (_, fnList) ->
      fnList.map { fn ->
        fn.parameters.map { r -> r.type.fastResolve() }
      }.flatten()
    }.flatten()
    val allReturnTypes = allApis.map { (_, fnList) ->
      fnList.mapNotNull {
        it.returnType?.fastResolve()
      }
    }.flatten()

    val handler = PropertyHandler(resolver, (allParameterTypes + allReturnTypes), environment.logger)
    val allTypes = handler.getAllClientTypes()

    // 构建服务
    val services = allApis.map { (c, e) ->
      val opts = e.map { operation ->
        val returnTypeName = operation.returnType!!.fastResolve().declaration.qualifiedNameAsString!!
        val returnType = if (returnTypeName == "kotlin.Unit") null else handler.getCopyClientTypeToReturnType(returnTypeName)?.run {
          val isNull = operation.returnType?.fastResolve()?.isMarkedNullable
          copy(
            nullable = if (isNull == true) true else null,
            usedGenerics = operation.returnType?.fastResolve()?.arguments?.toInputGenericTypeList()?.toMutableList() ?: mutableListOf()
          )
        }
        val params = operation.parameters.map { parameter ->
          val typeResolver = parameter.type.fastResolve()
          val type = handler.getCopyClientTypeToReturnType(typeResolver.declaration.qualifiedNameAsString!!)!!
          val inputGenerics = typeResolver.arguments.toInputGenericTypeList().toMutableList()
          ClientProp(
            name = parameter.name!!.asString(),
            typeName = type.typeName,
            nullable = if (typeResolver.isMarkedNullable) true else null,
            inputGenerics = inputGenerics
          )
        }.toMutableList()
        val pK = params.joinToString(":") { p -> p.typeName }

        val key = "${c.qualifiedNameAsString!!}#${operation.simpleNameAsString}::${pK}"

        ClientOperation(
          name = operation.simpleNameAsString,
          key = key,
          doc = operation.docString.toDoc(),
          parameterTypes = params,
          returnType = returnType,
        )
      }.toMutableList()

      ClientService(
        doc = c.docString.toDoc(), typeName = c.qualifiedNameAsString!!, operations = opts
      )
    }.toMutableList()

    // 组装模型
    val def = ClientApiStubs(
      services = services, definitions = allTypes.toMutableList()
    )

    if (nextHandles.isEmpty() && !generated) {
      environment.codeGenerator.createNewFile(Dependencies.ALL_FILES, "META-INF/compose-client", "${UUID.randomUUID()}-client-ts.stub", "json").use {
        OutputStreamWriter(it).use { r ->
          r.write(mapper.writeValueAsString(def))
        }
      }
      generated = true
    }
    return nextHandles
  }
}
