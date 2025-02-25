package net.yan100.compose.rds.core.listeners

import jakarta.annotation.Resource
import jakarta.persistence.PrePersist
import net.yan100.compose.core.generator.IOrderCodeGenerator
import net.yan100.compose.core.recursionFields
import net.yan100.compose.core.slf4j
import net.yan100.compose.rds.core.annotations.OrderCode
import net.yan100.compose.rds.core.entities.IJpaEntity
import org.springframework.stereotype.Component

private val log = slf4j(BizCodeInsertListener::class)

@Component
class BizCodeInsertListener {
  lateinit var bizCodeGenerator: IOrderCodeGenerator
    @Resource set

  init {
    log.debug("注册订单编号生成监听器")
  }

  @PrePersist
  fun insertBizCode(data: Any?) {
    data?.let { d ->
      d::class
        .recursionFields(IJpaEntity::class)
        .filter { it.isAnnotationPresent(OrderCode::class.java) }
        .map {
          it.trySetAccessible()
          it.getAnnotation(OrderCode::class.java) to it
        }
        .forEach {
          // 当 为 null 时进行设置
          if (it.second[data] == null) {
            it.second[data] = bizCodeGenerator.nextString()
          }
        }
    }
  }
}
