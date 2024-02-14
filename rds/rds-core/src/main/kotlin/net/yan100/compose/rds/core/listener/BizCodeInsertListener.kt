package net.yan100.compose.rds.core.listener

import jakarta.persistence.PrePersist
import net.yan100.compose.core.id.BizCodeGenerator
import net.yan100.compose.core.lang.recursionFields
import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.rds.core.annotations.BizCode
import net.yan100.compose.rds.core.entities.TreeEntity
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
    fun setBizCodeGenerator(v: BizCodeGenerator) {
        log.debug("设置当前订单编号生成器 = {}", v)
        this.bizCodeGenerator = v
    }

    @PrePersist
    fun insertBizCode(data: Any?) {
        data?.let { d ->
            d::class.recursionFields(TreeEntity::class).filter {
                it.isAnnotationPresent(BizCode::class.java)
            }.map {
                it.trySetAccessible()
                it.getAnnotation(BizCode::class.java) to it
            }.forEach {
                // 当 为 null 时进行设置
                if (it.second.get(data) == null) {
                    it.second.set(data, bizCodeGenerator.nextCodeStr())
                }
            }
        }
    }
}
