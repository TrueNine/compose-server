package com.truenine.component.rds.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.truenine.component.core.ctx.UserInfoContextHolder
import com.truenine.component.core.lang.LogKt
import com.truenine.component.rds.base.BaseDao
import com.truenine.component.rds.dao.TableRowDeleteRecordDao
import com.truenine.component.rds.models.TableRowChangeSerializableObjectModel
import com.truenine.component.rds.repo.TableRowDeleteRecordRepo
import com.truenine.component.rds.service.TableRowDeleteRecordService
import jakarta.persistence.Table
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*
import kotlin.reflect.full.findAnnotation

@Service
open class TableRowDeleteRecordServiceImpl(
  private val delRepo: TableRowDeleteRecordRepo,
  private val mapper: ObjectMapper
) : TableRowDeleteRecordService {

  private val log = LogKt.getLog(this::class)

  @Transactional(rollbackFor = [Exception::class])
  override fun save(data: Any?) = data?.run {
    val delRow = TableRowDeleteRecordDao()
    val userInfo = UserInfoContextHolder.get()
    delRow.apply {
      tableName = data::class.findAnnotation<Table>()?.name
      userId = userInfo?.userId
      userAccount = userInfo?.account
      deleteDatetime = LocalDateTime.now()
      entity = extractTableRow(data)
    }
    log.debug("保存删除的数据 = {}", delRow)
    delRepo.save(delRow)
  }

  private fun extractTableRow(data: Any?): TableRowChangeSerializableObjectModel? {
    return if (null != data && data is BaseDao) {
      TableRowChangeSerializableObjectModel().apply {
        id = data.id
        lang = "java"
        modelHash = Objects.hash(data::class)
        entityJson = mapper.writeValueAsString(data)
        namespace = data::class.simpleName
      }
    } else {
      log.warn("删除了空对象 = {}", data)
      null
    }
  }
}

