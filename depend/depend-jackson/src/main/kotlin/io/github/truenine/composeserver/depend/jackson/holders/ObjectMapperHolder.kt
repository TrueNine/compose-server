package io.github.truenine.composeserver.depend.jackson.holders

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.truenine.composeserver.depend.jackson.autoconfig.JacksonAutoConfiguration.Companion.DEFAULT_OBJECT_MAPPER_BEAN_NAME
import io.github.truenine.composeserver.depend.jackson.autoconfig.JacksonAutoConfiguration.Companion.NON_IGNORE_OBJECT_MAPPER_BEAN_NAME
import io.github.truenine.composeserver.logger
import jakarta.annotation.Resource
import org.springframework.stereotype.Component

/**
 * ObjectMapper配置持有者
 *
 * 提供统一的ObjectMapper访问接口，支持获取不同配置的ObjectMapper实例
 *
 * @author TrueNine
 * @since 2025-01-16
 */
@Component
class ObjectMapperHolder {

  @Resource(name = DEFAULT_OBJECT_MAPPER_BEAN_NAME) private lateinit var defaultMapper: ObjectMapper

  @Resource(name = NON_IGNORE_OBJECT_MAPPER_BEAN_NAME) private lateinit var nonIgnoreMapper: ObjectMapper

  /**
   * 获取默认的ObjectMapper
   *
   * @return 默认配置的ObjectMapper
   */
  fun getDefaultMapper(): ObjectMapper {
    log.debug("getting default ObjectMapper: {}", DEFAULT_OBJECT_MAPPER_BEAN_NAME)
    return defaultMapper
  }

  /**
   * 获取非忽略配置的ObjectMapper
   *
   * @return 非忽略配置的ObjectMapper
   */
  fun getNonIgnoreMapper(): ObjectMapper {
    log.debug("getting non-ignore ObjectMapper: {}", NON_IGNORE_OBJECT_MAPPER_BEAN_NAME)
    return nonIgnoreMapper
  }

  /**
   * 根据是否忽略未知属性获取相应的ObjectMapper
   *
   * @param ignoreUnknown 是否忽略未知属性，默认为true
   * @return 相应配置的ObjectMapper
   */
  fun getMapper(ignoreUnknown: Boolean = true): ObjectMapper {
    return if (ignoreUnknown) {
      log.debug("getting mapper with ignoreUnknown=true")
      defaultMapper
    } else {
      log.debug("getting mapper with ignoreUnknown=false")
      nonIgnoreMapper
    }
  }

  companion object {
    @JvmStatic private val log = logger<ObjectMapperHolder>()
  }
}
