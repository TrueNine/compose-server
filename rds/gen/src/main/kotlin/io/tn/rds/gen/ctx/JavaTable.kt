package io.tn.rds.gen.ctx

import io.tn.rds.gen.dao.ColumnIndex
import io.tn.rds.gen.dto.JavaColumnDto

data class JavaTable(
    var name: String? = null,
    var className: String? = null,
    var comment: String? = null,
    var idx: MutableList<ColumnIndex> = mutableListOf(),
    var columns: MutableList<JavaColumnDto> = mutableListOf(),
    var imports: MutableSet<String?> = mutableSetOf()
) {
  fun getEscapeComment(): String? {
    return this.comment?.replace("\"","\\\"")
  }
}
