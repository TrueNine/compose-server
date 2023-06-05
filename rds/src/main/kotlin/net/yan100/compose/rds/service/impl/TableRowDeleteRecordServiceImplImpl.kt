package net.yan100.compose.rds.service.impl

import com.fasterxml.jackson.databind.AnnotationIntrospector
import com.fasterxml.jackson.databind.ObjectMapper

import jakarta.persistence.Table

import net.yan100.compose.core.ctx.UserInfoContextHolder
import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.rds.base.BaseService
import net.yan100.compose.rds.base.BaseServiceImpl
import net.yan100.compose.rds.entity.TableRowDeleteRecord
import net.yan100.compose.rds.repository.TableRowDeleteRecordRepository
import net.yan100.compose.rds.service.TableRowDeleteRecordService
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import kotlin.reflect.full.findAnnotation

@Service
class TableRowDeleteRecordServiceImplImpl(
  private val delRepo: TableRowDeleteRecordRepository,
  private val mapper: ObjectMapper
) : TableRowDeleteRecordService,
  BaseService<TableRowDeleteRecord>,
  BaseServiceImpl<TableRowDeleteRecord>(delRepo) {

  private val log = slf4j(this::class)

  override fun saveAnyEntity(anyData: net.yan100.compose.rds.base.BaseEntity?): TableRowDeleteRecord? {
    return if (null == anyData) {
      log.debug("未对对象进行保存")
      null
    } else {
      val delRow = TableRowDeleteRecord()
      val userInfo = UserInfoContextHolder.get()
      delRow.apply {
        tableNames = anyData::class.findAnnotation<Table>()?.name
        userId = userInfo?.userId
        userAccount = userInfo?.account
        deleteDatetime = LocalDateTime.now()
        entity = extractTableRow(anyData)
      }
      log.trace("保存删除的数据 = {}", delRow)
      delRepo.save(delRow)
    }
  }

  private fun extractTableRow(anyData: net.yan100.compose.rds.base.AnyEntity): net.yan100.compose.rds.base.RecordModel {
    return net.yan100.compose.rds.base.RecordModel().apply {
      id = anyData.id
      lang = "java"
      modelHash = Objects.hash(anyData::class)
      entityJson = mapper.copy().setAnnotationIntrospector(AnnotationIntrospector.nopInstance()).writeValueAsString(anyData)
      namespace = anyData::class.java.name
    }
  }
}

