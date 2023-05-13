package net.yan100.compose.rds.listener

import jakarta.persistence.PrePersist
import net.yan100.compose.core.id.BizCodeGenerator
import net.yan100.compose.core.lang.recursionFields
import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.rds.annotations.BizCode
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
      d::class.recursionFields(net.yan100.compose.rds.base.BaseEntity::class).filter {
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
