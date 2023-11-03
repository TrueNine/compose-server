package net.yan100.compose.rds.core.listener

import jakarta.persistence.*
import net.yan100.compose.core.lang.slf4j
import org.springframework.stereotype.Component

/**
 * ## 在保存一个实体前，删除所有的外键属性
 * @author TrueNine
 * @since 2023-07-16
 */
@Component
class PreSaveDeleteReferenceListener {
  private val log = slf4j(this::class)

  @PrePersist
  fun deleteReference(attribute: Any?) {
    attribute?.let { attr ->
      attr.javaClass.declaredFields.filter {
        it.isAnnotationPresent(OneToOne::class.java)
          && it.isAnnotationPresent(OneToMany::class.java)
          && it.isAnnotationPresent(ManyToOne::class.java)
          && it.isAnnotationPresent(ManyToMany::class.java)
      }.forEach { fAttr ->
        log.debug("重置引用参数 = {}", fAttr)
        fAttr.set(attribute, null)
      }
    }
  }
}
