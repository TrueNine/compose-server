package com.truenine.component.rds.gen.dto

import com.truenine.component.rds.gen.ctx.JavaTable

data class JavaSchema(
  var name: String? = "",
  var tables: MutableList<JavaTable?> = mutableListOf()
)
