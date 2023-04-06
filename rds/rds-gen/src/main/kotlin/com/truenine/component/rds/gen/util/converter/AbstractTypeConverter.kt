package com.truenine.component.rds.gen.util.converter

abstract class AbstractTypeConverter {
  private val converterRule: MutableMap<String, CType> = mutableMapOf()
  private val defaultKey = "DATABASE_DEFAULT_CONVERTER_TYPE_NAMESPACE"

  init {
    converterRule[defaultKey] = CType("String", null)
  }

  protected fun putAll(converters: Map<String, CType>) {
    val b = mutableMapOf<String, CType>()
    converters.forEach {
      b[it.key.uppercase()] = it.value
    }
    this.converterRule.putAll(b)
  }

  fun getConverter(dbType: String): CType {
    val getC = converterRule.filter {
      dbType.uppercase().contains(it.key)
    }.map { it.value }
    return if (getC.isEmpty()) {
      converterRule[defaultKey]!!
    } else {
      getC[0]
    }
  }

  fun getConverters(): Map<String, CType> {
    return this.converterRule
  }

  fun findImports(types: List<String>): List<String> {
    return types.map {
      getConverter(it)
    }.map { it.importPkg }
      .filterNotNull()
  }
}
