package com.truenine.component.rds.gen.entity

data class ColumnIndexEntity(
  var table: String? = null,
  var columnName: String? = null,
  var comment: String? = null,
  var visible: Boolean = true,
  var nonUnique: Int? = 0,
  var seqInIndex: Long? = 1,
  var keyName: String? = null
) {
  fun getEscapeComment(): String? {
    return this.comment?.replace("\"", "\\\"")
  }
}
