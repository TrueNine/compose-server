package com.truenine.component.rds.listener

import com.truenine.component.core.lang.slf4j
import jakarta.persistence.*
import org.springframework.stereotype.Component

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
      }.forEach { attr ->
        log.trace("重置引用参数 = {}", attr)
        attr.set(attribute, null)
      }
    }
  }
}
