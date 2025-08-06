package io.github.truenine.composeserver.pay.wechat

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(title = "通知数据")
data class WechatPaySuccessVoResource(
  @Schema(title = "加密算法类型", description = "对开启结果数据进行加密的加密算法，目前只支持AEAD_AES_256_GCM") var algorithm: String? = null,
  @Schema(title = "数据密文") @JsonProperty("ciphertext") var cipherText: String? = null,
  @Schema(title = "附加数据") @JsonProperty("associated_data") var associatedDate: String? = null,
  @Schema(title = "原始类型", description = "原始回调类型，为transaction") @JsonProperty("original_type") var originalType: String? = null,
  @Schema(title = "随机串", description = "加密使用的随机串") @JsonProperty("nonce") var randomString: String? = null,
)
