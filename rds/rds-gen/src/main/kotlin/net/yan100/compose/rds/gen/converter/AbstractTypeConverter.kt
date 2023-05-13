package net.yan100.compose.rds.gen.converter

import net.yan100.compose.rds.gen.models.ConvertTypeModel

abstract class AbstractTypeConverter {
  private val converterRule: MutableMap<String, ConvertTypeModel> = mutableMapOf()
  private val defaultKey = "DATABASE_DEFAULT_CONVERTER_TYPE_NAMESPACE"

  init {
    converterRule[defaultKey] = ConvertTypeModel("String")
  }

  protected fun putAll(converters: Map<String, ConvertTypeModel>) {
    val b = mutableMapOf<String, ConvertTypeModel>()
    converters.forEach {
      b[it.key.uppercase()] = it.value
    }
    this.converterRule.putAll(b)
  }

  fun getConverterTypeModel(dbType: String): ConvertTypeModel {
    val getC = converterRule.filter {
      dbType.uppercase().contains(it.key)
    }.map { it.value }
    return if (getC.isEmpty()) {
      converterRule[defaultKey]!!
    } else {
      getC[0]
    }
  }

  fun getConverters(): Map<String, ConvertTypeModel> {
    return this.converterRule
  }

  fun findImports(types: List<String>): List<String> {
    return types.map {
      getConverterTypeModel(it)
    }.mapNotNull { it.importPkg }
  }
}
