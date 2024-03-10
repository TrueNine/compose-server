/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.rds.core.listener

import jakarta.persistence.*
import net.yan100.compose.core.log.slf4j
import org.springframework.stereotype.Component

/**
 * ## 在保存一个实体前，删除所有的外键属性
 *
 * @author TrueNine
 * @since 2023-07-16
 */
@Component
class PreSaveDeleteReferenceListener {
  private val log = slf4j(this::class)

  @PrePersist
  fun deleteReference(attribute: Any?) {
    attribute?.let { attr ->
      attr.javaClass.declaredFields
        .filter {
          it.isAnnotationPresent(OneToOne::class.java) &&
            it.isAnnotationPresent(OneToMany::class.java) &&
            it.isAnnotationPresent(ManyToOne::class.java) &&
            it.isAnnotationPresent(ManyToMany::class.java)
        }
        .forEach { fAttr ->
          log.debug("重置引用参数 = {}", fAttr)
          fAttr.set(attribute, null)
        }
    }
  }
}
