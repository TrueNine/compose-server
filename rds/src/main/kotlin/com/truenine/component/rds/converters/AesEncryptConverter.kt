package com.truenine.component.rds.converters

import com.truenine.component.core.encrypt.Encryptors
import com.truenine.component.core.encrypt.KeysRepository
import com.truenine.component.core.lang.LogKt
import com.truenine.component.core.lang.Str
import jakarta.annotation.Resource
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.springframework.stereotype.Component

@Converter
@Component
class AesEncryptConverter : AttributeConverter<String, String> {
  init {
    log.info("注册 aes 加密自动转换器 = {}", this)
  }

  @Resource
  private lateinit var keysRepo: KeysRepository

  override fun convertToDatabaseColumn(attribute: String?): String? =
    if (Str.hasText(attribute))
      Encryptors.encryptByAesKey(
        keysRepo.databaseEncryptAesSecret()!!,
        attribute!!
      ) else attribute

  override fun convertToEntityAttribute(dbData: String?): String? =
    if (Str.hasText(dbData))
      Encryptors.decryptByAesKey(
        keysRepo.databaseEncryptAesSecret()!!,
        dbData!!
      ) else dbData

  companion object {
    private val log = LogKt.getLog(AesEncryptConverter::class)
  }
}
