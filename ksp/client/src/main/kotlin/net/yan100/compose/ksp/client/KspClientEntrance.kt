package net.yan100.compose.ksp.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class KspClientEntrance : SymbolProcessorProvider {
  override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
    val mapper = ObjectMapper()
    mapper.enable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT)
    // 设置不序列化 null 值
    mapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    // 设置不序列化空对象
    mapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY)


    val kotlinModule = KotlinModule.Builder().build()
    mapper.registerModule(kotlinModule)

    return KspClientProcessor(environment, mapper)
  }
}
