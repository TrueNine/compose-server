package com.truenine.component.rds.gen.dao


data class DataBaseTable(
  var schema: String? = null,
  var name: String? = null,
  var comment: String? = null
) {
  fun getEscapeComment(): String? {
    return this.comment?.replace("\"","\\\"")
  }
}
