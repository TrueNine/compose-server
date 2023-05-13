package net.yan100.compose.rds.gen.models

import net.yan100.compose.rds.gen.ctx.TableContext

data class SchemaModel(
  var name: String? = "",
  var tables: MutableList<TableContext?> = mutableListOf()
)
