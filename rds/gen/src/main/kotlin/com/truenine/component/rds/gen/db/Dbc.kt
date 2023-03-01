package com.truenine.component.rds.gen.db

import cn.hutool.db.Db
import com.truenine.component.rds.gen.dao.ColumnIndex
import com.truenine.component.rds.gen.dao.DataBaseTable
import com.truenine.component.rds.gen.dao.TableColumn

object Dbc {
  private val db: Db = Db.use()
  fun queryDb(dbName: String): List<DataBaseTable> {
    return db.query(
      """
      SELECT 
        table_schema       `schema`,
        table_name      AS `name`,
        `table_comment` AS `comment`
      FROM 
        information_schema.tables
      WHERE 
        table_schema = ?;""".trimIndent(), dbName
    ).filterNotNull()
      .map {
        val table = DataBaseTable()
        table.schema = it.getStr("schema")
        table.name = it.getStr("name")
        table.comment = it.getStr("comment")
        table
      }
  }

  fun queryTable(tableName: String): List<TableColumn> {
    return db.query("SHOW FULL COLUMNS FROM `${tableName}`;")
      .filterNotNull()
      .map {
        val col = TableColumn()
        col.type = it.getStr("type")
        col.key = it.getStr("key")
        col.comment = it.getStr("comment")
        col.defaultValue = it.getStr("default")
        col.field = it.getStr("field")
        col.nullable = it.getBool("null")
        col
      }
  }

  fun queryIndex(tableName: String): List<ColumnIndex> {
    return db.query("SHOW INDEX FROM `${tableName}`;").filterNotNull()
      .map {
        val idx = ColumnIndex()
        idx.table = it.getStr("table")
        idx.nonUnique = it.getInt("non_unique")
        idx.columnName = it.getStr("column_name")
        idx.keyName = it.getStr("key_name")
        idx.visible = it.getBool("visible")
        idx.seqInIndex = it.getLong("seq_in_index")
        idx.comment = it.getStr("comment")
        idx
      }
  }
}
