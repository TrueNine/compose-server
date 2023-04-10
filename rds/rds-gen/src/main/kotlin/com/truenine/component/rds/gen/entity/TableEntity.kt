package com.truenine.component.rds.gen.entity


data class TableEntity(
  var schema: String? = null,
  var name: String? = null,
  var comment: String? = null
) {
  fun getEscapeComment(): String? {
    return this.comment?.replace("\"", "\\\"")
  }
}
