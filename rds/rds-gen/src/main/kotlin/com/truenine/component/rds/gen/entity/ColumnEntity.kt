package com.truenine.component.rds.gen.entity

data class ColumnEntity(
  var field: String? = null,
  var type: String? = null,
  var nullable: Boolean = false,
  var key: String? = null,
  var defaultValue: String? = null,
  var comment: String? = null,
) {
  fun getEscapeComment(): String? {
    return this.comment?.replace("\"", "\\\"")
  }
}
