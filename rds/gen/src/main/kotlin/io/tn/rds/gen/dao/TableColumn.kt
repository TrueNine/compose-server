package io.tn.rds.gen.dao

data class TableColumn(
  var field: String? = null,
  var type: String? = null,
  var nullable: Boolean = false,
  var key: String? = null,
  var defaultValue: String? = null,
  var comment: String? = null,
){
  fun getEscapeComment(): String? {
    return this.comment?.replace("\"","\\\"")
  }
}
