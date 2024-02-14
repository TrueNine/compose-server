package net.yan100.compose.rds.converters


import jakarta.annotation.Resource
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import net.yan100.compose.core.encrypt.Encryptors
import net.yan100.compose.core.encrypt.IKeysRepo
import net.yan100.compose.core.lang.slf4j
import org.springframework.stereotype.Component

@Converter
@Component
class AesEncryptConverter : AttributeConverter<String, String> {
    init {
        log.debug("注册 aes 加密converter = {}", AesEncryptConverter::class.java)
    }

    @Resource
    private lateinit var keysRepo: IKeysRepo

    override fun convertToDatabaseColumn(attribute: String?): String? =
        if (net.yan100.compose.core.lang.Str.hasText(attribute))
            Encryptors.encryptByAesKey(
                keysRepo.databaseEncryptAesSecret()!!,
                attribute!!
            ) else attribute

    override fun convertToEntityAttribute(dbData: String?): String? =
        if (net.yan100.compose.core.lang.Str.hasText(dbData))
            Encryptors.decryptByAesKey(
                keysRepo.databaseEncryptAesSecret()!!,
                dbData!!
            ) else dbData

    companion object {
        @JvmStatic
        private val log = slf4j(AesEncryptConverter::class)
    }
}
