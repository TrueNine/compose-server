/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.rds.gen.util

import cn.hutool.db.Db
import net.yan100.compose.rds.gen.entity.ColumnEntity
import net.yan100.compose.rds.gen.entity.ColumnIndexEntity
import net.yan100.compose.rds.gen.entity.TableEntity

object HutoolDbConnector {
  private val db: Db = Db.use()

  fun queryCurrentDbName(): String? {
    return db.queryString("SELECT DATABASE();")
  }

  fun queryDb(dbName: String): List<TableEntity> {
    return db
      .query(
        """
      SELECT 
        table_schema       `schema`,
        table_name      AS `name`,
        `table_comment` AS `comment`
      FROM 
        information_schema.tables
      WHERE 
        table_schema = ?;"""
          .trimIndent(),
        dbName
      )
      .filterNotNull()
      .map {
        val table = TableEntity()
        table.schema = it.getStr("schema")
        table.name = it.getStr("name")
        table.comment = it.getStr("comment")
        table
      }
  }

  fun queryColumnModel(tableName: String): List<ColumnEntity> {
    return db.query("SHOW FULL COLUMNS FROM `${tableName}`;").filterNotNull().map {
      val col = ColumnEntity()
      col.type = it.getStr("type")
      col.key = it.getStr("key")
      col.comment = it.getStr("comment")
      col.defaultValue = it.getStr("default")
      col.field = it.getStr("field")
      col.nullable = it.getBool("null")
      col
    }
  }

  fun queryIndex(tableName: String): List<ColumnIndexEntity> {
    return db.query("SHOW INDEX FROM `${tableName}`;").filterNotNull().map {
      val idx = ColumnIndexEntity()
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
