package net.yan100.compose.rds.gen.models

data class ColumnModel(
  var colName: String? = null,
  var upperName: String? = null,
  var fieldName: String? = null,
  var dbType: String? = null,
  var javaType: String? = null,
  var nullable: Boolean = false,
  var unique: Boolean = false,
  var defSql: String? = null,
  var defaultValue: String? = null,
  var comment: String? = null,
) {
  fun getEscapeComment(): String? {
    return this.comment?.replace("\"", "\\\"")
  }
}
