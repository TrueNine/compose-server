package com.truenine.component.rds.listener

import com.truenine.component.core.id.BizCodeGenerator
import com.truenine.component.core.lang.recursionFields
import com.truenine.component.core.lang.slf4j
import com.truenine.component.rds.annotations.BizCode
import com.truenine.component.rds.base.BaseEntity
import jakarta.persistence.PrePersist
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class BizCodeInsertListener {
  private lateinit var bizCodeGenerator: BizCodeGenerator
  private val log = slf4j(this::class)

  init {
    log.debug("注册订单编号生成监听器")
  }

  @Autowired
  fun setBizCodeGenerator(vals: BizCodeGenerator) {
    log.debug("设置当前订单编号生成器 = {}", vals)
    this.bizCodeGenerator = vals
  }

  @PrePersist
  fun insert(data: Any?) {
    data?.let { d ->
      d::class.recursionFields(BaseEntity::class).filter {
        it.isAnnotationPresent(BizCode::class.java)
      }.map {
        it.trySetAccessible()
        it.getAnnotation(BizCode::class.java) to it
      }.forEach {
        it.second.set(data, bizCodeGenerator.nextCodeStr())
      }
    }
  }
}
