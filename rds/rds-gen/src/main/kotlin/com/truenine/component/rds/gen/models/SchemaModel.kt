package com.truenine.component.rds.gen.models

import com.truenine.component.rds.gen.ctx.TableContext

data class SchemaModel(
  var name: String? = "",
  var tables: MutableList<TableContext?> = mutableListOf()
)
