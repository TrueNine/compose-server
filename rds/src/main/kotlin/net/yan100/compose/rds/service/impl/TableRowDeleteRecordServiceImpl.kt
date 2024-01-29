package net.yan100.compose.rds.service.impl

import com.fasterxml.jackson.databind.AnnotationIntrospector
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.Table
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
import java.time.LocalDateTime
import java.util.*
import kotlin.reflect.full.findAnnotation

@Service
class TableRowDeleteRecordServiceImpl(
  private val delRepo: ITableRowDeleteRecordRepository,
  private val mapper: ObjectMapper
) : ITableRowDeleteRecordService,
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
      entityJson = mapper.copy().setAnnotationIntrospector(AnnotationIntrospector.nopInstance()).writeValueAsString(anyData)
      namespace = anyData::class.java.name
    }
  }
}

