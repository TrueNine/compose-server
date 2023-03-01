package io.tn.rds.gen.dto

import io.tn.rds.gen.ctx.JavaTable

data class JavaSchema(
  var name: String? = "",
  var tables: MutableList<JavaTable?> = mutableListOf()
)
