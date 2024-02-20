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
package net.yan100.compose.rds.service.impl

import com.fasterxml.jackson.databind.AnnotationIntrospector
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.*
import kotlin.reflect.full.findAnnotation
import net.yan100.compose.core.ctx.UserInfoContextHolder
import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.rds.core.entities.AnyEntity
import net.yan100.compose.rds.core.entities.IEntity
import net.yan100.compose.rds.core.models.DataRecord
import net.yan100.compose.rds.entities.TableRowDeleteRecord
import net.yan100.compose.rds.repositories.ITableRowDeleteRecordRepository
import net.yan100.compose.rds.service.ITableRowDeleteRecordService
import net.yan100.compose.rds.service.base.CrudService
import net.yan100.compose.rds.service.base.IService
import org.springframework.stereotype.Service

@Service
class TableRowDeleteRecordServiceImpl(
  private val delRepo: ITableRowDeleteRecordRepository,
  private val mapper: ObjectMapper
) :
  ITableRowDeleteRecordService,
  IService<TableRowDeleteRecord>,
  CrudService<TableRowDeleteRecord>(delRepo) {

  private val log = slf4j(this::class)

  override fun saveAnyEntity(anyData: IEntity?): TableRowDeleteRecord? {
    return if (null == anyData) {
      log.debug("未对对象进行保存")
      null
    } else {
      val delRow = TableRowDeleteRecord()
      val userInfo = UserInfoContextHolder.get()
      delRow.apply {
        tableNames = anyData::class.findAnnotation<Table>()?.name!!
        userId = userInfo?.userId
        userAccount = userInfo?.account
        deleteDatetime = LocalDateTime.now()
        this.entity = extractTableRow(anyData)
      }
      log.trace("保存删除的数据 = {}", delRow)
      delRepo.save(delRow)
    }
  }

  private fun extractTableRow(anyData: AnyEntity): DataRecord {
    return DataRecord().apply {
      id = anyData.id
      lang = "java"
      modelHash = Objects.hash(anyData::class)
      entityJson =
        mapper
          .copy()
          .setAnnotationIntrospector(AnnotationIntrospector.nopInstance())
          .writeValueAsString(anyData)
      namespace = anyData::class.java.name
    }
  }
}
