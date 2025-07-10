package net.yan100.compose

import net.yan100.compose.annotations.SensitiveStrategy

@Deprecated("使用量很少")
interface ISensitiveScope<T> {
  fun String.addressDetails() = SensitiveStrategy.ADDRESS.desensitizeSerializer()(this)

  fun String.bankCard() = SensitiveStrategy.BANK_CARD_CODE.desensitizeSerializer()(this)

  fun String.chinaName() = SensitiveStrategy.NAME.desensitizeSerializer()(this)

  fun String.multipleName() = SensitiveStrategy.MULTIPLE_NAME.desensitizeSerializer()(this)

  fun String.chinaIdCard() = SensitiveStrategy.ID_CARD.desensitizeSerializer()(this)

  fun String.chinaPhone() = SensitiveStrategy.PHONE.desensitizeSerializer()(this)

  fun String.password() = SensitiveStrategy.PASSWORD.desensitizeSerializer()(this)

  fun String.email() = SensitiveStrategy.EMAIL.desensitizeSerializer()(this)

  fun String.once() = SensitiveStrategy.ONCE.desensitizeSerializer()(this)
}
