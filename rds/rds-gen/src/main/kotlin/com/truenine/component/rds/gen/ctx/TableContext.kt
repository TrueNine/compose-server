package com.truenine.component.rds.gen.ctx

import com.truenine.component.rds.gen.entity.ColumnIndexEntity
import com.truenine.component.rds.gen.models.ColumnModel

data class TableContext(
  var name: String? = null,
  var className: String? = null,
  var comment: String? = null,
  var idx: MutableList<ColumnIndexEntity> = mutableListOf(),
  var columns: MutableList<ColumnModel> = mutableListOf(),
  var imports: MutableSet<String?> = mutableSetOf()
) {
  fun getIdxUpper(): MutableList<ColumnIndexEntity> {
    return idx.map {
      it.keyName = it.keyName?.uppercase()
      it.columnName = it.columnName?.uppercase()
      it
    }.toMutableList()
  }

  fun getEscapeComment(): String? {
    return this.comment?.replace("\"", "\\\"")
  }
}