package com.truenine.component.rds.service.impl

import com.fasterxml.jackson.databind.AnnotationIntrospector
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.truenine.component.core.ctx.UserInfoContextHolder
import com.truenine.component.core.lang.LogKt
import com.truenine.component.rds.base.*
import com.truenine.component.rds.entity.TableRowDeleteRecordEntity
import com.truenine.component.rds.repository.TableRowDeleteRecordRepository
import com.truenine.component.rds.service.TableRowDeleteRecordService
import jakarta.persistence.Table
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import kotlin.reflect.full.findAnnotation

@Service
class TableRowDeleteRecordServiceImplImpl(
  private val delRepo: TableRowDeleteRecordRepository,
  private val mapper: ObjectMapper
) : TableRowDeleteRecordService,
  BaseService<TableRowDeleteRecordEntity>,
  BaseServiceImpl<TableRowDeleteRecordEntity>(delRepo) {

  private val log = LogKt.getLog(this::class)

  override fun saveAnyEntity(anyData: BaseEntity?): TableRowDeleteRecordEntity? {
    return if (null == anyData) {
      log.debug("未对对象进行保存")
      null
    } else {
      val delRow = TableRowDeleteRecordEntity()
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

  private fun extractTableRow(anyData: AnyEntity): RecordModel? {
    return RecordModel().apply {
      id = anyData.id
      lang = "java"
      modelHash = Objects.hash(anyData::class)
      entityJson = mapper.copy().setAnnotationIntrospector(AnnotationIntrospector.nopInstance()).writeValueAsString(anyData)
      namespace = anyData::class.java.name
    }
  }
}

