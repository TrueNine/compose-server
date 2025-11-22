package io.github.truenine.composeserver.depend.jackson.holders

import io.github.truenine.composeserver.depend.jackson.autoconfig.JacksonAutoConfiguration.Companion.DEFAULT_OBJECT_MAPPER_BEAN_NAME
import io.github.truenine.composeserver.depend.jackson.autoconfig.JacksonAutoConfiguration.Companion.NON_IGNORE_OBJECT_MAPPER_BEAN_NAME
import io.github.truenine.composeserver.logger
import jakarta.annotation.Resource
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper

/**
 * ObjectMapper configuration holder.
 *
 * Provides unified access to ObjectMapper instances with different configurations.
 *
 * @author TrueNine
 * @since 2025-01-16
 */
@Component
class ObjectMapperHolder {

  @Resource(name = DEFAULT_OBJECT_MAPPER_BEAN_NAME) private lateinit var defaultMapper: ObjectMapper

  @Resource(name = NON_IGNORE_OBJECT_MAPPER_BEAN_NAME) private lateinit var nonIgnoreMapper: ObjectMapper

  /**
   * Get the default ObjectMapper.
   *
   * @return Default configured ObjectMapper
   */
  fun getDefaultMapper(): ObjectMapper {
    log.debug("getting default ObjectMapper: {}", DEFAULT_OBJECT_MAPPER_BEAN_NAME)
    return defaultMapper
  }

  /**
   * Get the ObjectMapper that does not ignore unknown properties.
   *
   * @return Non-ignore configured ObjectMapper
   */
  fun getNonIgnoreMapper(): ObjectMapper {
    log.debug("getting non-ignore ObjectMapper: {}", NON_IGNORE_OBJECT_MAPPER_BEAN_NAME)
    return nonIgnoreMapper
  }

  /**
   * Get ObjectMapper according to whether unknown properties should be ignored.
   *
   * @param ignoreUnknown Whether to ignore unknown properties, default true
   * @return ObjectMapper with corresponding configuration
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
